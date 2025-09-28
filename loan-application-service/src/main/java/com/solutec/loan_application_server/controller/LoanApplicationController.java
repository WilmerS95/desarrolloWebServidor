package com.solutec.loan_application_server.controller;

import com.solutec.loan_application_server.dto.LoanApplicationRequest;
import com.solutec.loan_application_server.dto.LoanApplicationResponse;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import com.solutec.loan_application_server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/loan-applications")
public class LoanApplicationController {

    private static final Logger log =
            LoggerFactory.getLogger(LoanApplicationController.class);

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String getLoans(Authentication authentication) {
        return "Acceso a préstamos autorizado para usuario Loan: " + authentication.getName();
    }

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
        la.setApplicationDate(LocalDateTime.now());
        la.setStatus("PENDING");
        la = loanApplicationRepository.save(la);

        emailService.sendEmail(
            user.getEmail(),
            "Tu solicitud de empeño ha sido recibida",
            userEmailTemplate(user, la, item)
        );

        List<User> admins = userRepository.findAll().stream()
            .filter(u -> u.getRole() != null &&
                ("ADMIN".equalsIgnoreCase(u.getRole().getRoleName()) || "SUPER_ADMIN".equalsIgnoreCase(u.getRole().getRoleName())))
            .toList();

        for (User admin : admins) {
            emailService.sendEmail(
                admin.getEmail(),
                "Nueva solicitud de empeño #" + la.getLoanApplicationID(),
                adminEmailTemplate(user, la, item)
            );
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
                                    <a href="http://192.168.1.35:4200/admin/solicitudes/%d" style="
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
            la.getLoanApplicationID()
        );
    }
}
