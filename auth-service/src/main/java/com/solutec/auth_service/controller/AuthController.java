package com.solutec.auth_service.controller;

import com.solutec.auth_service.entity.*;
import com.solutec.auth_service.repository.UserRepository;
import com.solutec.auth_service.service.EmailService;
import com.solutec.auth_service.service.JwtService;
import com.solutec.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        String token = jwtService.generateToken(user);
                        return ResponseEntity.ok(new LoginResponse(token));
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Contraseña incorrecta"));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuario no encontrado")));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("field", "username", "message", "El usuario ya existe"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("field", "email", "message", "El correo ya está registrado"));
        }

        userService.register(request);
        return ResponseEntity.ok(Map.of("message", "Usuario creado correctamente"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        var userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Correo no encontrado"));
        }

        String token = userService.createPasswordResetToken(userOptional.get());
        String resetLink = "http://192.168.1.33:4200/reset-password?token=" + token;

        try {
            emailService.sendEmail(
                    email,
                    "Recuperar contraseña",
                    "<p>Hola, para restablecer tu contraseña haz clic en el siguiente enlace:</p>" +
                            "<a href=\"" + resetLink + "\">Restablecer contraseña</a>"
            );
            return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al enviar correo"));
        }
    }

    @GetMapping("/test-email")
    public ResponseEntity<?> testEmail() {
        String email = "krodasa7@miumg.edu.gt";
        //var userOptional = userService.findByEmail(email);


        //String token = userService.createPasswordResetToken(userOptional.get());
        String resetLink = "http://localhost:4200/reset-password?token=" + "token";
        try {
            emailService.sendEmail(
                    email,
                    "Nueva Prueba",
                    "<p>Hola, para restablecer tu contraseña haz clic en el siguiente enlace:</p>" +
                            "<a href=\"" + resetLink + "\">Restablecer contraseña</a>"
            );
            return ResponseEntity.ok("Correo de recuperación enviado a" + email);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar correo");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        boolean result = userService.resetPassword(token, newPassword);

        if (result) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Token inválido o expirado"));
        }
    }
}