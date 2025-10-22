package com.solutec.loan_application_server.controller;

import com.solutec.loan_application_server.dto.*;
import com.solutec.loan_application_server.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Cliente reporta un pago
     * POST /api/payments/report
     */
    @PostMapping("/report")
    public ResponseEntity<?> reportPayment(
            @RequestBody PaymentRequestDTO request,
            Authentication authentication) {
        try {
            log.info("Reportando pago para préstamo: {}", request.getLoanId());
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

    /**
     * Administrador revisa un pago (aprobar/rechazar)
     * PUT /api/payments/review
     */
    @PutMapping("/review")
    public ResponseEntity<?> reviewPayment(
            @RequestBody PaymentReviewDTO reviewDTO,
            Authentication authentication) {
        try {
            // Obtener userID del admin desde el token JWT
            Long adminUserId = getUserIdFromAuth(authentication);

            log.info("Admin {} revisando pago {}", adminUserId, reviewDTO.getPaymentId());

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

    /**
     * Obtener pagos pendientes de revisión (para admin)
     * GET /api/payments/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingPayments() {
        try {
            List<PaymentDTO> payments = paymentService.getPendingPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos pendientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener historial de pagos de un préstamo
     * GET /api/payments/loan/{loanId}
     */
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<?> getLoanPayments(@PathVariable Long loanId) {
        try {
            List<PaymentDTO> payments = paymentService.getLoanPayments(loanId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos del préstamo {}", loanId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener estado de cuenta completo de un préstamo
     * GET /api/payments/statement/{loanId}
     */
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

    /**
     * Helper para extraer userId del token JWT
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        // Implementar según tu configuración de JWT
        // Por ejemplo, si usas OAuth2:
        // Map<String, Object> attributes = ((OAuth2AuthenticationToken) authentication)
        //         .getPrincipal().getAttributes();
        // return Long.parseLong(attributes.get("userId").toString());

        // Por ahora, retornamos un ID de ejemplo
        return 1L; // CAMBIAR ESTO según tu implementación de JWT
    }
}