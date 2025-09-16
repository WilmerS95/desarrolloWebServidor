package com.solutec.auth_service.service;

import com.solutec.auth_service.dto.ApiResponse;
import com.solutec.auth_service.entity.*;
import com.solutec.auth_service.exception.PasswordReuseException;
import com.solutec.auth_service.repository.AuditLogRepository;
import com.solutec.auth_service.repository.PasswordResetTokenRepository;
import com.solutec.auth_service.repository.RoleRepository;
import com.solutec.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final AuditLogRepository auditLogRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Role defaultRole = roleRepository.findByRoleName("CLIENTE")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRoleName("CLIENTE");
                    return roleRepository.save(newRole);
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setSecondOrMoreNames(request.getSecondOrMoreNames());
        user.setFirstLastName(request.getFirstLastName());
        user.setSecondLastName(request.getSecondLastName());
        user.setMarriedLastName(request.getMarriedLastName());
        user.setTelephone(request.getTelephone());
        user.setAddress(request.getAddress());
        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);

        /*AuditLog log = new AuditLog();
        log.setChangedBy(savedUser);
        log.setEntityType("User");
        log.setEntityID(savedUser.getUserID());
        log.setAction("CHANGE_PASSWORD");
        log.setOldState(null);
        log.setNewState(savedUser.getPassword());
        log.setChangeDate(java.time.LocalDateTime.now());
        auditLogRepository.save(log);*/

        return new UserResponse(savedUser.getUserID(), savedUser.getUsername(), savedUser.getEmail());
    }

    public String createPasswordResetToken(User user) {
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = java.util.UUID.randomUUID().toString();

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setToken(token);
        prt.setExpiryDate(java.time.LocalDateTime.now().plusHours(1));

        tokenRepository.save(prt);
        return token;
    }

    public ApiResponse resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> prtOpt = tokenRepository.findByToken(token);

        if (prtOpt.isEmpty() || prtOpt.get().getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            return new ApiResponse(false, "El enlace ya expiró o es inválido");
        }

        User user = prtOpt.get().getUser();

        /*user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);*/

        try {
            changePasswordWithAudit(user, newPassword);
        } catch (PasswordReuseException ex) {
            return new ApiResponse(false, ex.getMessage());
        }

        tokenRepository.delete(prtOpt.get());
        return new ApiResponse(true, "Contraseña actualizada correctamente");
    }

    private void changePasswordWithAudit(User user, String newPassword) {
        List<AuditLog> lastPasswords = auditLogRepository
                .findTop5ByEntityTypeAndEntityIDAndActionOrderByChangeDateDesc(
                        "User", user.getUserID(), "CHANGE_PASSWORD"
                );

        for (AuditLog log : lastPasswords) {
            if (passwordEncoder.matches(newPassword, log.getNewState())) {
                throw new PasswordReuseException("No puede usar las últimas 5 contraseñas");
            }
        }

        String oldPasswordHash = user.getPassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        AuditLog log = new AuditLog();
        log.setChangedBy(user);
        log.setEntityType("User");
        log.setEntityID(user.getUserID());
        log.setAction("CHANGE_PASSWORD");
        log.setOldState(oldPasswordHash);
        log.setNewState(user.getPassword());
        log.setChangeDate(java.time.LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
