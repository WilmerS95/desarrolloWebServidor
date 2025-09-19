package com.solutec.auth_service.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/loan-applications")
    public String getLoans(Authentication authentication) {
        return "Acceso a préstamos autorizado para usuario: " + authentication.getName();
    }
}
