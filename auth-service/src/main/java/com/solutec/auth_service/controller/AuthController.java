package com.solutec.auth_service.controller;

import com.solutec.auth_service.entity.LoginRequest;
import com.solutec.auth_service.entity.LoginResponse;
import com.solutec.auth_service.entity.RegisterRequest;
import com.solutec.auth_service.entity.UserResponse;
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
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado"));
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        var userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        String token = userService.createPasswordResetToken(userOptional.get());
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        try {
            emailService.sendEmail(
                    email,
                    "Recuperar contraseña",
                    "<p>Hola, para restablecer tu contraseña haz clic en el siguiente enlace:</p>" +
                            "<a href=\"" + resetLink + "\">Restablecer contraseña</a>"
            );
            return ResponseEntity.ok("Correo de recuperación enviado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar correo");
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
}