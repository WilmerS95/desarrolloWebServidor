package com.solutec.auth_service.service;

import com.solutec.auth_service.entity.*;
import com.solutec.auth_service.repository.PasswordResetTokenRepository;
import com.solutec.auth_service.repository.RoleRepository;
import com.solutec.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponse register(RegisterRequest request) {
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
        user.setRole(defaultRole);

        User savedUser = userRepository.save(user);

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

    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> prtOpt = tokenRepository.findByToken(token);
        if (prtOpt.isEmpty() || prtOpt.get().getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }

        User user = prtOpt.get().getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(prtOpt.get());
        return true;
    }
}
