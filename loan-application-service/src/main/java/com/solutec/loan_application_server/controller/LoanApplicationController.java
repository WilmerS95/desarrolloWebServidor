package com.solutec.loan_application_server.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan-applications")
public class LoanApplicationController {
    @GetMapping
    public String getLoans(Authentication authentication) {
        return "Acceso a préstamos autorizado para usuario: " + authentication.getName();
    }
}
