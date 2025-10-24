package com.solutec.loan_application_server.controller;

import com.solutec.loan_application_server.dto.LoanApplicationRequest;
import com.solutec.loan_application_server.dto.LoanApplicationResponse;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import com.solutec.loan_application_server.service.*;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.solutec.loan_application_server.util.JwtUtil;
import com.solutec.loan_application_server.dto.LoanDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/loan-applications")
public class LoanApplicationController {

    private static final Logger log =
            LoggerFactory.getLogger(LoanApplicationController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private InstallmentCalculationService installmentCalculationService;

    @Autowired
    private ProposedInstallmentRepository proposedInstallmentRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ContractGenerationService contractGenerationService;

    @Autowired
    private LoanService loanService;

    @GetMapping
    public String getLoans(Authentication authentication) {
        return "Acceso a préstamos autorizado para usuario Loan: " + authentication.getName();
    }

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    private final FirebaseStorageService storageService;
    private final ItemRepository itemRepository;
    private final ItemPhotoRepository itemPhotoRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public LoanApplicationController(
            FirebaseStorageService storageService,
            ItemRepository itemRepository,
            ItemPhotoRepository itemPhotoRepository,
            LoanApplicationRepository loanApplicationRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.storageService = storageService;
        this.itemRepository = itemRepository;
        this.itemPhotoRepository = itemPhotoRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/admin-emails")
    public List<String> getAdminEmails() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null &&
                        (u.getRole().getRoleName().equalsIgnoreCase("ADMIN")
                                || u.getRole().getRoleName().equalsIgnoreCase("SUPER_ADMIN")))
                .map(User::getEmail)
                .toList();
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLoanApplication(
            @RequestPart("data") LoanApplicationRequest data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Jwt jwt) throws Exception {

        Object userIdObj = jwt.getClaims().get("userId");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body("userId claim missing in token");
        }
        Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue()
                : Long.parseLong(userIdObj.toString());

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no encontrado");
        }

        User user = userOpt.get();

        if (data.getRequestedAmount() == null || data.getRequestedAmount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "El monto solicitado es obligatorio y debe ser mayor a 0"
            ));
        }

        Item item = new Item();
        item.setNameItem(data.getNameItem());
        item.setBrand(data.getBrand());
        item.setDescription(data.getDescription());
        item.setSpecification(data.getSpecification());
        item.setCategoryId(data.getCategoryId());
        item = itemRepository.save(item);

        if (files != null && !files.isEmpty()) {
            List<String> urls = storageService.uploadMultipleFiles(files, "items/" + item.getItemID());
            for (String url : urls) {
                ItemPhoto ip = new ItemPhoto();
                ip.setItem(item);
                ip.setPhotoPath(url);
                itemPhotoRepository.save(ip);
            }
        }

        LoanApplication la = new LoanApplication();
        la.setUser(userOpt.get());
        la.setItem(item);
        la.setQuantityPayments(data.getQuantityPayments());
        la.setRequestedAmount(data.getRequestedAmount());
        la.setApplicationDate(LocalDateTime.now());
        la.setStatus("PENDIENTE");
        la = loanApplicationRepository.save(la);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Tu solicitud de empeño ha sido recibida",
                    userEmailTemplate(user, la, item)
            );
            List<User> admins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && ("ADMIN".equalsIgnoreCase(u.getRole().getRoleName()) || "SUPER_ADMIN".equalsIgnoreCase(u.getRole().getRoleName()))).toList();

            for (User admin : admins) {
                emailService.sendEmail(
                        admin.getEmail(),
                        "Nueva solicitud de empeño #" + la.getLoanApplicationID(),
                        adminEmailTemplate(user, la, item)
                );
            }
        } catch (Exception e) {
            log.error("Error enviando correos de solicitud de empeño", e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "error", "No se pudo enviar los correos de notificación",
                            "detalle", e.getMessage()
                    ));
        }

        List<String> photoUrls = itemPhotoRepository.findByItem(item).stream()
                .map(ItemPhoto::getPhotoPath)
                .toList();

        LoanApplicationResponse response = new LoanApplicationResponse(
                la.getLoanApplicationID(),
                item.getItemID(),
                item.getNameItem(),
                item.getBrand(),
                la.getQuantityPayments(),
                la.getRequestedAmount(),
                la.getApplicationDate(),
                la.getStatus(),
                photoUrls
        );

        return ResponseEntity.ok(response);
    }

    private String userEmailTemplate(User user, LoanApplication la, Item item) {
        return """
                <html>
                <body style="margin:0; padding:0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color:#f4f4f4;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.1); overflow:hidden;">
                                    <tr>
                                        <td style="padding:40px; text-align:center;">
                                            <h1 style="color:#2C3E50; margin-bottom:20px;">Solicitud de empeño recibida</h1>
                                            <p style="color:#555; font-size:16px; line-height:1.5;">
                                                Hola <b>%s</b>, hemos recibido tu solicitud de empeño.
                                            </p>
                                            <table style="margin:20px auto; text-align:left; font-size:16px; color:#333;">
                                                <tr><td><b>Artículo:</b></td><td>%s</td></tr>
                                                <tr><td><b>Marca:</b></td><td>%s</td></tr>
                                                <tr><td><b>Pagos:</b></td><td>%d</td></tr>
                                            </table>
                                            <p style="color:#555; font-size:16px;">
                                                Pronto te avisaremos si es aprobada, rechazada o si existe una contrapropuesta.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color:#f1f1f1; padding:20px; text-align:center; font-size:12px; color:#aaaaaa;">
                                            &copy; 2025 Solutec Loan Service – Este es un mensaje automático, por favor no respondas.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(user.getFirstName(), item.getNameItem(), item.getBrand(), la.getQuantityPayments());
    }

    private String adminEmailTemplate(User user, LoanApplication la, Item item) {
        StringBuilder photos = new StringBuilder();
        itemPhotoRepository.findByItem(item).forEach(ip ->
                photos.append("<img src='")
                        .append(ip.getPhotoPath())
                        .append("' width='180' style='margin:5px;border-radius:6px;border:1px solid #ddd;'/>")
        );

        return """
                <html>
                <body style="margin:0; padding:0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color:#f4f4f4;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.1); overflow:hidden;">
                                    <tr>
                                        <td style="padding:40px;">
                                            <h1 style="color:#2C3E50; text-align:center; margin-bottom:20px;">Nueva solicitud de empeño</h1>
                                            <p style="color:#555; font-size:16px; line-height:1.5;">
                                                <b>Cliente:</b> %s %s<br>
                                                <b>Email:</b> %s
                                            </p>
                                            <table style="margin:20px auto; text-align:left; font-size:16px; color:#333;">
                                                <tr><td><b>Artículo:</b></td><td>%s (%s)</td></tr>
                                                <tr><td><b>Pagos:</b></td><td>%d</td></tr>
                                            </table>
                                            <h3 style="color:#2C3E50; text-align:center;">Fotos del artículo</h3>
                                            <div style="text-align:center;">%s</div>
                                            <div style="text-align:center; margin-top:30px;">
                                                <a href="%s/admin/solicitudes/%d" style="
                                                    display:inline-block;
                                                    padding:15px 30px;
                                                    font-size:16px;
                                                    color:#ffffff;
                                                    background-color:#28a745;
                                                    text-decoration:none;
                                                    border-radius:5px;
                                                    font-weight:bold;
                                                ">Revisar Solicitud</a>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color:#f1f1f1; padding:20px; text-align:center; font-size:12px; color:#aaaaaa;">
                                            &copy; 2025 Solutec Loan Service – Este es un mensaje automático, por favor no respondas.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                user.getFirstName(),
                user.getFirstLastName(),
                user.getEmail(),
                item.getNameItem(),
                item.getBrand(),
                la.getQuantityPayments(),
                photos.toString(),
                frontendBaseUrl,
                la.getLoanApplicationID()
        );
    }

    /*@PutMapping("/admin/accept/{id}")
    public ResponseEntity<?> acceptLoanApplication(@PathVariable Long id) {
        return updateStatusWithOptionalComment(id, "ACEPTADO", null);
    }*/

    @PutMapping("/admin/reject/{id}")
    public ResponseEntity<?> rejectLoanApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String comment = body.get("comment");
        if (comment == null || comment.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El motivo es obligatorio para rechazar una solicitud"));
        }
        return updateStatusWithOptionalComment(id, "RECHAZADO", comment);
    }

    @PutMapping("/admin/counteroffer/{id}")
    public ResponseEntity<?> counterOfferLoanApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String comment = body.get("comment");
        if (comment == null || comment.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El motivo es obligatorio para enviar una contraoferta"));
        }
        return updateStatusWithOptionalComment(id, "CONTRAOFERTADO", comment);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<LoanApplicationResponse>> getAllApplications() {
        List<LoanApplicationResponse> list = loanApplicationRepository.findAll()
                .stream()
                .map(la -> {
                    List<String> photoUrls = itemPhotoRepository.findByItem(la.getItem())
                            .stream()
                            .map(ItemPhoto::getPhotoPath)
                            .toList();
                    return new LoanApplicationResponse(
                            la.getLoanApplicationID(),
                            la.getItem().getItemID(),
                            la.getItem().getNameItem(),
                            la.getItem().getBrand(),
                            la.getQuantityPayments(),
                            la.getRequestedAmount(),
                            la.getApplicationDate(),
                            la.getStatus(),
                            photoUrls
                    );
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        return loanApplicationRepository.findById(id)
                .map(la -> {
                    List<String> photoUrls = itemPhotoRepository.findByItem(la.getItem())
                            .stream()
                            .map(ItemPhoto::getPhotoPath)
                            .toList();
                    return ResponseEntity.ok(
                            new LoanApplicationResponse(
                                    la.getLoanApplicationID(),
                                    la.getItem().getItemID(),
                                    la.getItem().getNameItem(),
                                    la.getItem().getBrand(),
                                    la.getQuantityPayments(),
                                    la.getRequestedAmount(),
                                    la.getApplicationDate(),
                                    la.getStatus(),
                                    photoUrls
                            )
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El campo 'status' es obligatorio"));
        }

        return loanApplicationRepository.findById(id)
                .map(la -> {
                    la.setStatus(newStatus.toUpperCase());
                    loanApplicationRepository.save(la);

                    try {
                        emailService.sendEmail(
                                la.getUser().getEmail(),
                                "Actualización de tu solicitud de empeño",
                                """
                                        <html><body style="font-family:Arial,sans-serif">
                                        <h2>Estado actualizado</h2>
                                        <p>Hola %s, tu solicitud #%d ha sido actualizada a estado: <b>%s</b>.</p>
                                        </body></html>
                                        """.formatted(
                                        la.getUser().getFirstName(),
                                        la.getLoanApplicationID(),
                                        newStatus.toUpperCase()
                                )
                        );
                    } catch (MessagingException e) {
                        log.error("Error enviando correo de actualización de estado", e);
                        return ResponseEntity.status(500).body(Map.of(
                                "error", "No se pudo enviar el correo de notificación",
                                "detalle", e.getMessage()
                        ));
                    }

                    return ResponseEntity.ok(Map.of(
                            "message", "Estado actualizado a " + newStatus.toUpperCase(),
                            "loanApplicationID", la.getLoanApplicationID()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> updateStatusWithOptionalComment(Long id, String newStatus, String comment) {
        return loanApplicationRepository.findById(id)
                .map(la -> {
                    la.setStatus(newStatus);
                    loanApplicationRepository.save(la);

                    try {
                        String subject = "Actualización de tu solicitud de empeño";
                        String message = """
                                <html><body style="font-family:Arial,sans-serif">
                                <h2>Estado actualizado</h2>
                                <p>Hola %s, tu solicitud #%d ha sido actualizada a estado: <b>%s</b>.</p>
                                %s
                                <p>Gracias por usar nuestros servicios.</p>
                                </body></html>
                                """.formatted(
                                la.getUser().getFirstName(),
                                la.getLoanApplicationID(),
                                newStatus,
                                (comment != null ? "<p><b>Motivo:</b> " + comment + "</p>" : "")
                        );

                        emailService.sendEmail(
                                la.getUser().getEmail(),
                                subject,
                                message
                        );

                    } catch (Exception e) {
                        log.error("Error enviando correo de actualización de estado", e);
                        return ResponseEntity.status(500).body(Map.of(
                                "error", "No se pudo enviar el correo de notificación",
                                "detalle", e.getMessage()
                        ));
                    }

                    return ResponseEntity.ok(Map.of(
                            "message", "Solicitud actualizada a estado " + newStatus,
                            "loanApplicationID", la.getLoanApplicationID()
                    ));
                })
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Solicitud no encontrada con ID " + id)));
    }

    @PutMapping("/admin/accept/{id}")
    public ResponseEntity<?> acceptLoanApplication(@PathVariable Long id) {
        try {
            Optional<LoanApplication> optApp = loanApplicationRepository.findById(id);
            if (optApp.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Solicitud no encontrada"));
            }

            LoanApplication app = optApp.get();

            if (!"PENDIENTE".equalsIgnoreCase(app.getStatus()) &&
                    !"PENDING".equalsIgnoreCase(app.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "La solicitud no está pendiente"));
            }

            if (app.getRequestedAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "La solicitud no tiene monto solicitado. Debe ingresarse antes de aprobar."
                ));
            }

            Double approvedAmount = app.getRequestedAmount();

            List<ProposedInstallment> installments = installmentCalculationService
                    .calculateInstallments(approvedAmount, app.getQuantityPayments());

            for (ProposedInstallment inst : installments) {
                inst.setLoanApplication(app);
            }

            proposedInstallmentRepository.saveAll(installments);

            app.setStatus("APROBADO_PENDIENTE_CLIENTE");
            app.setApprovedAmount(approvedAmount);
            app.setClientAccepted(false);
            loanApplicationRepository.save(app);

            sendInstallmentNotificationToClient(app, installments);

            return ResponseEntity.ok(Map.of(
                    "message", "Solicitud aprobada. Se ha enviado notificación al cliente.",
                    "loanApplicationId", app.getLoanApplicationID(),
                    "installments", installments.stream().map(this::mapInstallmentToDTO).toList()
            ));

        } catch (Exception e) {
            log.error("Error aceptando solicitud", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/client/accept-installments/{id}")
    public ResponseEntity<?> clientAcceptsInstallments(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            Long userId = extractUserIdFromAuthentication(authentication);

            Optional<LoanApplication> optApp = loanApplicationRepository.findById(id);
            if (optApp.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Solicitud no encontrada"));
            }

            LoanApplication app = optApp.get();

            if (!app.getUser().getUserID().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
            }

            if (!"APROBADO_PENDIENTE_CLIENTE".equalsIgnoreCase(app.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "La solicitud no está en estado correcto"));
            }

            app.setClientAccepted(true);
            app.setStatus("CLIENTE_ACEPTO");
            loanApplicationRepository.save(app);

            Loan loan = createLoanFromApplication(app);

            sendContractConfirmationEmail(app, loan);

            return ResponseEntity.ok(Map.of(
                    "message", "¡Contrato creado exitosamente!",
                    "loanId", loan.getLoanId(),
                    "loanApplicationId", app.getLoanApplicationID()
            ));

        } catch (Exception e) {
            log.error("Error al aceptar cuotas", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/client/installment-proposal/{id}")
    public ResponseEntity<?> getInstallmentProposal(@PathVariable Long id) {
        try {
            List<ProposedInstallment> installments = proposedInstallmentRepository
                    .findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(id);

            if (installments.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "No hay propuesta de cuotas"));
            }

            BigDecimal total = installmentCalculationService.getTotalAmount(installments);

            return ResponseEntity.ok(Map.of(
                    "installments", installments.stream().map(this::mapInstallmentToDTO).toList(),
                    "totalAmount", total,
                    "numberOfInstallments", installments.size()
            ));

        } catch (Exception e) {
            log.error("Error obteniendo propuesta de cuotas", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error: " + e.getMessage()));
        }
    }

    private Loan createLoanFromApplication(LoanApplication app) {

        List<ProposedInstallment> installments = proposedInstallmentRepository
                .findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(
                        app.getLoanApplicationID()
                );

        if (installments.isEmpty()) {
            throw new RuntimeException("No se encontraron cuotas propuestas para esta aplicación");
        }

        BigDecimal loanAmount = BigDecimal.valueOf(app.getApprovedAmount());

        BigDecimal totalAmount = installments.stream()
                .map(ProposedInstallment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInterest = totalAmount.subtract(loanAmount);

        Loan loan = new Loan();
        loan.setLoanApplication(app);
        loan.setApprovalDate(LocalDateTime.now());
        loan.setLoanAmount(loanAmount);
        loan.setInterestRate(new BigDecimal("5.00")); // 5% mensual
        loan.setTerm(app.getQuantityPayments());

        loan.setTotalInterest(totalInterest);
        loan.setTotalAmount(totalAmount);
        loan.setBalance(totalAmount);

        loan.setDueDate(LocalDateTime.now().plusDays(30L * (app.getQuantityPayments() + 1)));
        loan.setStatus("ACTIVO");

        loan.setContractNumber(contractGenerationService.generateContractNumber());
        loan.setContractSignatureHash(
                contractGenerationService.generateSignatureHash(
                        loan.getContractNumber(),
                        app.getUser().getUserID(),
                        LocalDateTime.now()
                )
        );
        loan.setContractGeneratedDate(LocalDateTime.now());
        loan.setLatePaymentFee(new BigDecimal("50.00")); // Mora diaria
        loan.setGracePeriodDays(30); // 30 días de gracia
        loan.setDefaultDays(90); // 90 días para considerar incumplimiento
        Loan savedLoan = loanRepository.save(loan);

        log.info("✅ Préstamo creado - ID: {}, Capital: {}, Interés: {}, Total: {}, Balance: {}",
                savedLoan.getLoanId(), loanAmount, totalInterest, totalAmount, totalAmount);

        return savedLoan;
    }

    private void sendInstallmentNotificationToClient(
            LoanApplication app,
            List<ProposedInstallment> installments) {

        User user = app.getUser();
        StringBuilder installmentDetails = new StringBuilder();

        BigDecimal total = BigDecimal.ZERO;
        for (ProposedInstallment inst : installments) {
            total = total.add(inst.getAmount());
            installmentDetails.append(String.format(
                    "<tr><td>Cuota %d</td><td>GTQ %.2f</td><td>%s</td></tr>",
                    inst.getInstallmentNumber(),
                    inst.getAmount(),
                    inst.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
        }

        String emailBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color:#f4f4f4; padding:20px;">
                <div style="max-width:600px; margin:0 auto; background-color:#fff; border-radius:10px; padding:30px;">
                    <h1 style="color:#2C3E50;">¡Tu solicitud ha sido aprobada!</h1>
                    <p>Hola <b>%s</b>,</p>
                    <p>Tu solicitud de empeño ha sido <b style="color:#28a745;">APROBADA</b>.</p>
                    
                    <h3>Detalle del préstamo:</h3>
                    <table style="width:100%%; border-collapse:collapse; margin:20px 0;">
                        <tr style="background-color:#f8f9fa;">
                            <th style="padding:10px; border:1px solid #ddd;">Concepto</th>
                            <th style="padding:10px; border:1px solid #ddd;">Valor</th>
                        </tr>
                        <tr>
                            <td style="padding:10px; border:1px solid #ddd;">Monto aprobado</td>
                            <td style="padding:10px; border:1px solid #ddd;"><b>GTQ %.2f</b></td>
                        </tr>
                        <tr>
                            <td style="padding:10px; border:1px solid #ddd;">Número de cuotas</td>
                            <td style="padding:10px; border:1px solid #ddd;">%d pagos mensuales</td>
                        </tr>
                        <tr>
                            <td style="padding:10px; border:1px solid #ddd;">Total a pagar</td>
                            <td style="padding:10px; border:1px solid #ddd;"><b>GTQ %.2f</b></td>
                        </tr>
                    </table>
                    
                    <h3>Plan de cuotas:</h3>
                    <table style="width:100%%; border-collapse:collapse; margin:20px 0;">
                        <thead>
                            <tr style="background-color:#007bff; color:white;">
                                <th style="padding:10px; border:1px solid #ddd;">Cuota</th>
                                <th style="padding:10px; border:1px solid #ddd;">Monto</th>
                                <th style="padding:10px; border:1px solid #ddd;">Fecha de vencimiento</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                    
                    <div style="background-color:#fff3cd; border-left:4px solid #ffc107; padding:15px; margin:20px 0;">
                        <p style="margin:0;"><b>Importante:</b> Debes aceptar este plan de cuotas para finalizar el proceso.</p>
                    </div>
                    
                    <div style="text-align:center; margin-top:30px;">
                        <a href="%s/loan-application/%d/accept" 
                           style="background-color:#28a745; color:white; padding:15px 30px; text-decoration:none; border-radius:5px; display:inline-block;">
                            Aceptar plan de cuotas
                        </a>
                    </div>
                    
                    <p style="margin-top:30px; color:#666; font-size:12px;">
                        Si tienes dudas, contáctanos. Este correo es automático, no respondas a este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """,
                user.getFirstName(),
                app.getApprovedAmount(),
                installments.size(),
                total,
                installmentDetails.toString(),
                frontendBaseUrl,
                app.getLoanApplicationID()
        );

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Solicitud Aprobada - Plan de cuotas",
                    emailBody
            );
        } catch (Exception e) {
            log.error("Error enviando notificación de cuotas", e);
        }
    }

    private void sendContractConfirmationEmail(LoanApplication app, Loan loan) {
        User user = app.getUser();
        String emailBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <div style="max-width:600px; margin:0 auto; padding:20px;">
                    <h1 style="color:#28a745;">✓ ¡Contrato Firmado!</h1>
                    <p>Hola <b>%s</b>,</p>
                    <p>Has aceptado el plan de cuotas exitosamente.</p>
                    <p><b>Número de contrato:</b> %d</p>
                    <p><b>Monto del préstamo:</b> GTQ %.2f</p>
                    <p>Puedes recoger tu dinero en nuestras oficinas presentando tu DPI.</p>
                    <p>Recuerda realizar tus pagos puntualmente.</p>
                </div>
            </body>
            </html>
            """,
                user.getFirstName(),
                loan.getLoanId(),
                loan.getLoanAmount()
        );

        try {
            emailService.sendEmail(user.getEmail(), "Contrato de empeño confirmado", emailBody);
        } catch (Exception e) {
            log.error("Error enviando confirmación de contrato", e);
        }
    }

    private Map<String, Object> mapInstallmentToDTO(ProposedInstallment inst) {
        return Map.of(
                "installmentNumber", inst.getInstallmentNumber(),
                "amount", inst.getAmount(),
                "dueDate", inst.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    private Long extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Object userIdClaim = jwt.getClaim("userId");
            if (userIdClaim != null) {
                return userIdClaim instanceof Number
                        ? ((Number) userIdClaim).longValue()
                        : Long.parseLong(userIdClaim.toString());
            }
        }
        throw new RuntimeException("No se pudo extraer el userId del token");
    }

    @GetMapping("/my-history")
    public ResponseEntity<List<LoanApplicationResponse>> getMyApplicationHistory(
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(401).body(null);
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            List<LoanApplication> applications = loanApplicationRepository
                    .findByUser_UserIDOrderByApplicationDateDesc(userId);

            List<LoanApplicationResponse> response = applications.stream()
                    .map(la -> {
                        List<String> photoUrls = itemPhotoRepository.findByItem(la.getItem())
                                .stream()
                                .map(ItemPhoto::getPhotoPath)
                                .toList();

                        return new LoanApplicationResponse(
                                la.getLoanApplicationID(),
                                la.getItem().getItemID(),
                                la.getItem().getNameItem(),
                                la.getItem().getBrand(),
                                la.getQuantityPayments(),
                                la.getRequestedAmount(),
                                la.getApplicationDate(),
                                la.getStatus(),
                                photoUrls
                        );
                    })
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo historial de solicitudes", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/my-history/{id}")
    public ResponseEntity<?> getMyApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            return loanApplicationRepository.findById(id)
                    .map(la -> {
                        if (!la.getUser().getUserID().equals(userId)) {
                            return ResponseEntity.status(403)
                                    .body(Map.of("error", "No tienes permiso para ver esta solicitud"));
                        }

                        List<String> photoUrls = itemPhotoRepository.findByItem(la.getItem())
                                .stream()
                                .map(ItemPhoto::getPhotoPath)
                                .toList();

                        return ResponseEntity.ok(
                                new LoanApplicationResponse(
                                        la.getLoanApplicationID(),
                                        la.getItem().getItemID(),
                                        la.getItem().getNameItem(),
                                        la.getItem().getBrand(),
                                        la.getQuantityPayments(),
                                        la.getRequestedAmount(),
                                        la.getApplicationDate(),
                                        la.getStatus(),
                                        photoUrls
                                )
                        );
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            log.error("Error obteniendo solicitud", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno"));
        }
    }

    @GetMapping("/my-history/{id}/contract")
    public ResponseEntity<?> getMyContract(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            Optional<LoanApplication> appOpt = loanApplicationRepository.findById(id);
            if (appOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            LoanApplication app = appOpt.get();

            if (!app.getUser().getUserID().equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
            }

            Optional<Loan> loanOpt = loanRepository.findByLoanApplicationLoanApplicationID(id);
            if (loanOpt.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "No se encontró contrato para esta solicitud"));
            }

            Loan loan = loanOpt.get();

            List<ProposedInstallment> installments = proposedInstallmentRepository
                    .findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(id);

            Map<String, Object> response = new HashMap<>();
            response.put("loanId", loan.getLoanId());
            response.put("loanApplicationId", app.getLoanApplicationID());
            response.put("itemName", app.getItem().getNameItem());
            response.put("approvalDate", loan.getApprovalDate());
            response.put("loanAmount", loan.getLoanAmount());
            response.put("interestRate", loan.getInterestRate());
            response.put("term", loan.getTerm());
            response.put("dueDate", loan.getDueDate());
            response.put("status", loan.getStatus());
            response.put("balance", loan.getBalance());
            response.put("installments", installments.stream().map(inst -> {
                Map<String, Object> instMap = new HashMap<>();
                instMap.put("installmentNumber", inst.getInstallmentNumber());
                instMap.put("amount", inst.getAmount());
                instMap.put("dueDate", inst.getDueDate());
                instMap.put("status", "PENDIENTE");
                return instMap;
            }).toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo contrato", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno"));
        }
    }

    @GetMapping("/my-contracts")
    public ResponseEntity<?> getMyContracts(@AuthenticationPrincipal Jwt jwt) {
        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            List<LoanApplication> applications = loanApplicationRepository
                    .findByUser_UserIDOrderByApplicationDateDesc(userId)
                    .stream()
                    .filter(app -> "CLIENTE_ACEPTO".equals(app.getStatus()))
                    .toList();

            List<Map<String, Object>> contracts = new ArrayList<>();

            for (LoanApplication app : applications) {
                Optional<Loan> loanOpt = loanRepository
                        .findByLoanApplicationLoanApplicationID(app.getLoanApplicationID());

                if (loanOpt.isPresent()) {
                    Loan loan = loanOpt.get();

                    Map<String, Object> contractMap = new HashMap<>();
                    contractMap.put("loanId", loan.getLoanId());
                    contractMap.put("loanApplicationId", app.getLoanApplicationID());
                    contractMap.put("itemName", app.getItem().getNameItem());
                    contractMap.put("itemBrand", app.getItem().getBrand());
                    contractMap.put("loanAmount", loan.getLoanAmount());
                    contractMap.put("approvalDate", loan.getApprovalDate());
                    contractMap.put("status", loan.getStatus());
                    contractMap.put("balance", loan.getBalance());
                    contractMap.put("term", loan.getTerm());

                    contracts.add(contractMap);
                }
            }

            return ResponseEntity.ok(contracts);

        } catch (Exception e) {
            log.error("Error obteniendo contratos", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno"));
        }
    }

    @GetMapping("/my-history/{id}/contract-html")
    public ResponseEntity<?> getContractHtml(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            Optional<LoanApplication> appOpt = loanApplicationRepository.findById(id);
            if (appOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Solicitud no encontrada");
            }

            LoanApplication app = appOpt.get();

            if (!app.getUser().getUserID().equals(userId)) {
                return ResponseEntity.status(403).body("No autorizado");
            }

            Optional<Loan> loanOpt = loanRepository.findByLoanApplicationLoanApplicationID(id);
            if (loanOpt.isEmpty()) {
                return ResponseEntity.status(404).body("No existe contrato para esta solicitud");
            }

            Loan loan = loanOpt.get();

            // Si no tiene contrato generado, generarlo ahora
            if (loan.getContractNumber() == null || loan.getContractSignatureHash() == null) {
                loan.setContractNumber(contractGenerationService.generateContractNumber());
                loan.setContractSignatureHash(
                        contractGenerationService.generateSignatureHash(
                                loan.getContractNumber(),
                                userId,
                                LocalDateTime.now()
                        )
                );
                loan.setContractGeneratedDate(LocalDateTime.now());
                loanRepository.save(loan);
            }

            List<ProposedInstallment> installments = proposedInstallmentRepository
                    .findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(id);

            String contractHtml = contractGenerationService.generateContractHtml(
                    loan,
                    app,
                    app.getUser(),
                    app.getItem(),
                    installments
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set("Content-Disposition", "inline; filename=contrato-" + loan.getContractNumber() + ".html");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(contractHtml);

        } catch (Exception e) {
            log.error("Error generando contrato HTML", e);
            return ResponseEntity.status(500).body("Error generando contrato: " + e.getMessage());
        }
    }

    @GetMapping("/my-loans")
    public ResponseEntity<?> getMyActiveLoans(@AuthenticationPrincipal Jwt jwt) {
        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            log.info("Obteniendo préstamos activos del usuario: {}", userId);

            List<Loan> loans = loanRepository.findAll().stream()
                    .filter(loan -> {
                        if (loan.getLoanApplication() != null &&
                                loan.getLoanApplication().getUser() != null) {
                            return loan.getLoanApplication().getUser().getUserID().equals(userId);
                        }
                        return false;
                    })
                    .filter(loan -> {
                        String status = loan.getStatus();
                        return "ACTIVE".equalsIgnoreCase(status) ||
                                "ACTIVO".equalsIgnoreCase(status) ||  // ✅ Español
                                "PENDING_PAYMENT".equalsIgnoreCase(status) ||
                                "PAGO_PENDIENTE".equalsIgnoreCase(status) ||
                                "PENDIENTE".equalsIgnoreCase(status);
                    })
                    .toList();

            List<Map<String, Object>> loanDTOs = loans.stream()
                    .map(loan -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("loanId", loan.getLoanId());
                        dto.put("loanAmount", loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO);
                        dto.put("interestRate", loan.getInterestRate());
                        dto.put("term", loan.getTerm());
                        dto.put("totalInterest", loan.getTotalInterest());
                        dto.put("totalAmount", loan.getTotalAmount());
                        dto.put("balance", loan.getBalance() != null ? loan.getBalance() : BigDecimal.ZERO);
                        dto.put("status", loan.getStatus());
                        dto.put("itemName", loan.getLoanApplication().getItem().getNameItem());
                        dto.put("approvalDate", loan.getApprovalDate());
                        dto.put("disbursementDate", loan.getContractGeneratedDate());
                        return dto;
                    })
                    .toList();

            return ResponseEntity.ok(loanDTOs);

        } catch (Exception e) {
            log.error("Error obteniendo préstamos del usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-loans/all")
    public ResponseEntity<?> getAllMyLoans(@AuthenticationPrincipal Jwt jwt) {
        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            log.info("Obteniendo todos los préstamos del usuario: {}", userId);

            List<Loan> loans = loanRepository.findAll().stream()
                    .filter(loan -> {
                        if (loan.getLoanApplication() != null &&
                                loan.getLoanApplication().getUser() != null) {
                            return loan.getLoanApplication().getUser().getUserID().equals(userId);
                        }
                        return false;
                    })
                    .toList();

            List<Map<String, Object>> loanDTOs = loans.stream()
                    .map(loan -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("loanId", loan.getLoanId());
                        dto.put("loanAmount", loan.getLoanAmount());
                        dto.put("interestRate", loan.getInterestRate());
                        dto.put("term", loan.getTerm());
                        dto.put("totalInterest", loan.getTotalInterest());
                        dto.put("totalAmount", loan.getTotalAmount());
                        dto.put("balance", loan.getBalance());
                        dto.put("status", loan.getStatus());
                        dto.put("itemName", loan.getLoanApplication().getItem().getNameItem());
                        dto.put("approvalDate", loan.getApprovalDate());
                        dto.put("disbursementDate", loan.getContractGeneratedDate());
                        return dto;
                    })
                    .toList();

            return ResponseEntity.ok(loanDTOs);

        } catch (Exception e) {
            log.error("Error obteniendo todos los préstamos del usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}