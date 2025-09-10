package com.solutec.login_service.controller;

import com.solutec.desarrollo_web_server.entity.LoginRequest;
import com.solutec.desarrollo_web_server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping
    public String login(@RequestBody LoginRequest request) {
        boolean success = authService.login(request.getUsername(), request.getPassword());
        return success ? "Login exitoso" : "Credenciales incorrectas";
    }
}