package com.solutec.loan_application_server.controller;

import com.solutec.loan_application_server.dto.LoanApplicationRequest;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import com.solutec.loan_application_server.service.FirebaseStorageService;
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

        return ResponseEntity.ok(la);
    }
}
