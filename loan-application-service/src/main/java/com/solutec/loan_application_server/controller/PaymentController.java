package com.solutec.loan_application_server.controller;

import com.solutec.loan_application_server.dto.*;
import com.solutec.loan_application_server.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/report")
    public ResponseEntity<?> reportPayment(
            @RequestBody PaymentRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            log.info("Usuario {} reportando pago para préstamo: {}", userId, request.getLoanId());

            PaymentDTO payment = paymentService.reportPayment(request);

            return ResponseEntity.ok(Map.of(
                    "message", "Pago reportado exitosamente. Está en revisión.",
                    "payment", payment
            ));
        } catch (Exception e) {
            log.error("Error reportando pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/review")
    public ResponseEntity<?> reviewPayment(
            @RequestBody PaymentReviewDTO reviewDTO,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Long adminUserId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            log.info("Usuario {} revisando pago {}", adminUserId, reviewDTO.getPaymentId());

            PaymentDTO payment = paymentService.reviewPayment(reviewDTO, adminUserId);

            return ResponseEntity.ok(Map.of(
                    "message", "Pago revisado exitosamente",
                    "payment", payment
            ));
        } catch (Exception e) {
            log.error("Error revisando pago", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingPayments(@AuthenticationPrincipal Jwt jwt) {
        try {
            log.info("Obteniendo pagos pendientes");

            List<PaymentDTO> payments = paymentService.getPendingPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos pendientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<?> getLoanPayments(
            @PathVariable Long loanId,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            Object userIdObj = jwt.getClaims().get("userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            Long userId = (userIdObj instanceof Number)
                    ? ((Number) userIdObj).longValue()
                    : Long.parseLong(userIdObj.toString());

            log.info("Usuario {} obteniendo pagos del préstamo {}", userId, loanId);

            List<PaymentDTO> payments = paymentService.getLoanPayments(loanId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos del préstamo {}", loanId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statement/{loanId}")
    public ResponseEntity<?> getAccountStatement(@PathVariable Long loanId) {
        try {
            AccountStatementDTO statement = paymentService.getAccountStatement(loanId);
            return ResponseEntity.ok(statement);
        } catch (Exception e) {
            log.error("Error obteniendo estado de cuenta del préstamo {}", loanId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}