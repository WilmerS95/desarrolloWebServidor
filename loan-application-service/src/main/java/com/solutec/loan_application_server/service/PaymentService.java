package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.dto.*;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    /**
     * Cliente reporta un pago
     */
    @Transactional
    public PaymentDTO reportPayment(PaymentRequestDTO request) {
        // Validar préstamo existe
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        // Validar que el número de pago sea válido
        List<PaymentSchedule> schedules = paymentScheduleRepository
                .findByLoan_LoanIdAndPaymentNumber(request.getLoanId(), request.getPaymentNumber());

        if (schedules.isEmpty()) {
            throw new RuntimeException("Número de pago inválido");
        }

        PaymentSchedule schedule = schedules.get(0);

        // Crear nuevo pago con estado PENDIENTE
        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setPaymentNumber(request.getPaymentNumber());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("PENDIENTE");

        // Guardar comprobante si existe
        if (request.getReferenceBase64() != null && !request.getReferenceBase64().isEmpty()) {
            try {
                payment.setReference(Base64.getDecoder().decode(request.getReferenceBase64()));
            } catch (Exception e) {
                log.error("Error decodificando imagen de referencia", e);
            }
        }

        payment = paymentRepository.save(payment);

        // Enviar notificación al cliente
        notificationService.notifyPaymentReported(loan.getLoanApplication().getUser(), payment);

        log.info("Pago reportado: {} para préstamo {}", payment.getPaymentId(), loan.getLoanId());

        return convertToDTO(payment);
    }

    /**
     * Administrador revisa y aprueba/rechaza un pago
     */
    @Transactional
    public PaymentDTO reviewPayment(PaymentReviewDTO reviewDTO, Long adminUserId) {
        Payment payment = paymentRepository.findById(reviewDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (!"PENDIENTE".equals(payment.getStatus())) {
            throw new RuntimeException("Este pago ya fue revisado");
        }

        payment.setStatus(reviewDTO.getStatus());
        payment.setReviewComment(reviewDTO.getComment());
        payment.setReviewedBy(adminUserId);
        payment.setReviewDate(LocalDateTime.now());

        if ("APROBADO".equals(reviewDTO.getStatus())) {
            // Aplicar el pago al préstamo
            applyPaymentToLoan(payment);
        }

        payment = paymentRepository.save(payment);

        // Notificar al cliente sobre la revisión
        notificationService.notifyPaymentReviewed(
                payment.getLoan().getLoanApplication().getUser(),
                payment
        );

        log.info("Pago {} revisado por admin {}: {}",
                payment.getPaymentId(), adminUserId, reviewDTO.getStatus());

        return convertToDTO(payment);
    }

    /**
     * Aplica el pago al préstamo y actualiza el estado de cuenta
     */
    private void applyPaymentToLoan(Payment payment) {
        Loan loan = payment.getLoan();
        PaymentSchedule schedule = paymentScheduleRepository
                .findByLoan_LoanIdAndPaymentNumber(loan.getLoanId(), payment.getPaymentNumber())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Cronograma de pago no encontrado"));

        // Actualizar cronograma de pago
        schedule.setStatus("PAGADO");
        schedule.setPaidAmount(payment.getAmountPaid());
        schedule.setPaidDate(payment.getPaymentDate().toLocalDate());
        paymentScheduleRepository.save(schedule);

        // Actualizar balance del préstamo
        BigDecimal newBalance = loan.getBalance().subtract(payment.getAmountPaid());
        loan.setBalance(newBalance);

        // Si el balance es 0, marcar como PAID
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus("PAID");
        }

        loanRepository.save(loan);

        log.info("Pago aplicado al préstamo {}. Nuevo balance: {}", loan.getLoanId(), newBalance);
    }

    /**
     * Obtiene todos los pagos pendientes de revisión
     */
    public List<PaymentDTO> getPendingPayments() {
        return paymentRepository.findByStatus("PENDIENTE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de pagos de un préstamo
     */
    public List<PaymentDTO> getLoanPayments(Long loanId) {
        return paymentRepository.findByLoan_LoanIdOrderByPaymentDateDesc(loanId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el estado de cuenta completo de un préstamo
     */
    public AccountStatementDTO getAccountStatement(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        List<PaymentScheduleDTO> schedules = paymentScheduleRepository
                .findByLoan_LoanIdOrderByPaymentNumberAsc(loanId)
                .stream()
                .map(this::convertScheduleToDTO)
                .collect(Collectors.toList());

        List<PaymentDTO> payments = getLoanPayments(loanId);

        BigDecimal paidAmount = loan.getTotalAmount().subtract(loan.getBalance());
        int paidPayments = (int) schedules.stream()
                .filter(s -> "PAGADO".equals(s.getStatus()))
                .count();

        AccountStatementDTO statement = new AccountStatementDTO();
        statement.setLoanId(loan.getLoanId());
        statement.setLoanAmount(loan.getLoanAmount());
        statement.setTotalInterest(loan.getTotalInterest());
        statement.setTotalAmount(loan.getTotalAmount());
        statement.setBalance(loan.getBalance());
        statement.setPaidAmount(paidAmount);
        statement.setStatus(loan.getStatus());
        statement.setItemName(loan.getLoanApplication().getItem().getNameItem());
        statement.setTotalPayments(loan.getTerm());
        statement.setPaidPayments(paidPayments);
        statement.setPaymentSchedule(schedules);
        statement.setPayments(payments);

        return statement;
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setLoanId(payment.getLoan().getLoanId());
        dto.setPaymentNumber(payment.getPaymentNumber());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmountPaid(payment.getAmountPaid());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setReviewComment(payment.getReviewComment());
        dto.setReviewDate(payment.getReviewDate());
        dto.setReference(payment.getReference());
        return dto;
    }

    private PaymentScheduleDTO convertScheduleToDTO(PaymentSchedule schedule) {
        PaymentScheduleDTO dto = new PaymentScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setPaymentNumber(schedule.getPaymentNumber());
        dto.setDueDate(schedule.getDueDate().toString());
        dto.setAmountDue(schedule.getAmountDue());
        dto.setPrincipalAmount(schedule.getPrincipalAmount());
        dto.setInterestAmount(schedule.getInterestAmount());
        dto.setStatus(schedule.getStatus());
        dto.setPaidAmount(schedule.getPaidAmount());
        dto.setPaidDate(schedule.getPaidDate() != null ? schedule.getPaidDate().toString() : null);
        return dto;
    }
}