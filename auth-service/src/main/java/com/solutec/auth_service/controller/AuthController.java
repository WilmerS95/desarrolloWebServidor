package com.solutec.auth_service.controller;

import com.solutec.auth_service.dto.ApiResponse;
import com.solutec.auth_service.dto.LoginRequest;
import com.solutec.auth_service.dto.LoginResponse;
import com.solutec.auth_service.entity.*;
import com.solutec.auth_service.repository.UserRepository;
import com.solutec.auth_service.service.EmailService;
import com.solutec.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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

    private final JwtEncoder jwtEncoder;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return userRepository.findByUsername(loginRequest.getUsername())
                .map(user -> {
                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        Instant now = Instant.now();
                        long expiry = 3600L;

                        JwtClaimsSet claims = JwtClaimsSet.builder()
                                .issuer("auth-service")
                                .issuedAt(Instant.now())
                                .expiresAt(Instant.now().plusSeconds(3600))
                                .subject(user.getUsername())
                                .claim("role", user.getRole() != null ? user.getRole().getRoleName() : null)
                                .claim("userId", user.getUserID())
                                .claim("email", user.getEmail())
                                .build();

                        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

                        return ResponseEntity.ok(new LoginResponse(token));
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("message", "Usuario o contraseña incorrectos"));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Usuario no encontrado")));
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

        try {
            emailService.sendEmail(
                    email,
                    "Recuperar contraseña",
                    getEmailContent(token, frontendBaseUrl)
            );
            return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al enviar correo"));
        }
    }

    private static String getEmailContent(String token, String frontendBaseUrl) {
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        return """
        <html>
        <body style="margin:0; padding:0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f4;">
            <table width="100%%" cellpadding="0" cellspacing="0" style="padding: 40px 0;">
                <tr>
                    <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); overflow: hidden;">
                            <tr>
                                <td style="padding: 40px; text-align: center;">
                                    <h1 style="color: #2C3E50; margin-bottom: 20px;">Restablece tu contraseña</h1>
                                    <p style="color: #555555; font-size: 16px; line-height: 1.5;">
                                        Hola, hemos recibido una solicitud para restablecer tu contraseña.<br>
                                        Haz clic en el botón de abajo para continuar:
                                    </p>
                                    <a href="%s" style="
                                        display: inline-block;
                                        padding: 15px 30px;
                                        margin: 30px 0;
                                        font-size: 16px;
                                        color: #ffffff;
                                        background-color: #007BFF;
                                        text-decoration: none;
                                        border-radius: 5px;
                                        font-weight: bold;
                                    ">Restablecer contraseña</a>
                                    <p style="color: #999999; font-size: 14px; line-height: 1.4;">
                                        Si no solicitaste este cambio, puedes ignorar este correo.
                                    </p>
                                </td>
                            </tr>
                            <tr>
                                <td style="background-color: #f1f1f1; padding: 20px; text-align: center; font-size: 12px; color: #aaaaaa;">
                                    Este es un mensaje automático, por favor no respondas.<br>
                                    &copy; 2025 Solutec Auth Service
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.formatted(resetLink);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        ApiResponse response = userService.resetPassword(token, newPassword);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}