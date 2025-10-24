package com.solutec.loan_application_server.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public static Long extractUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return null;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return extractUserIdFromJwt(jwt);
    }

    public static Long extractUserIdFromJwt(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        Object userIdObj = jwt.getClaims().get("userId");
        if (userIdObj == null) {
            return null;
        }

        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        }

        try {
            return Long.valueOf(userIdObj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String extractUsername(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return null;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }

    public static String extractRole(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return null;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object roleObj = jwt.getClaims().get("role");
        return roleObj != null ? roleObj.toString() : null;
    }

    public static String extractEmail(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return null;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object emailObj = jwt.getClaims().get("email");
        return emailObj != null ? emailObj.toString() : null;
    }

    public static boolean hasRole(Authentication authentication, String requiredRole) {
        String userRole = extractRole(authentication);
        return userRole != null && userRole.equalsIgnoreCase(requiredRole);
    }

    public static boolean isAdmin(Authentication authentication) {
        String role = extractRole(authentication);
        return "ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role);
    }
}