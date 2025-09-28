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
            <h2>Solicitud recibida</h2>
            <p>Hola %s, hemos recibido tu solicitud de empeño.</p>
            <p><b>Artículo:</b> %s</p>
            <p><b>Marca:</b> %s</p>
            <p><b>Pagos:</b> %d</p>
            <p>Pronto te avisaremos si es aprobada, rechazada o si existe una contrapropuesta.</p>
            """.formatted(user.getFirstName(), item.getNameItem(), item.getBrand(), la.getQuantityPayments());
    }

    private String adminEmailTemplate(User user, LoanApplication la, Item item) {
        StringBuilder photos = new StringBuilder();
        itemPhotoRepository.findByItem(item).forEach(ip ->
            photos.append("<img src='")
                .append(ip.getPhotoPath())
                .append("' width='200' style='margin:5px;'/>")
        );

        return """
            <h2>Nueva solicitud de empeño</h2>
            <p><b>Cliente:</b> %s %s</p>
            <p><b>Email:</b> %s</p>
            <p><b>Artículo:</b> %s (%s)</p>
            <p><b>Pagos:</b> %d</p>
            <h3>Fotos:</h3>%s
            <p><a href='http://192.168.1.35:4200/admin/solicitudes/%d'
                  style='background-color:#4CAF50;color:white;
                         padding:10px 20px;text-decoration:none;'>
                  Revisar Solicitud</a></p>
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
