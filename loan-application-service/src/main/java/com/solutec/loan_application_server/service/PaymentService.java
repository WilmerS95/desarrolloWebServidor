package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.dto.*;
import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final ProposedInstallmentRepository proposedInstallmentRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public PaymentDTO reportPayment(PaymentRequestDTO request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setPaymentNumber(request.getPaymentNumber());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");

        if (request.getReferenceBase64() != null && !request.getReferenceBase64().isEmpty()) {
            try {
                byte[] referenceBytes = Base64.getDecoder().decode(request.getReferenceBase64());
                payment.setReference(referenceBytes);
            } catch (Exception e) {
                log.error("Error decodificando referencia base64", e);
            }
        }

        payment = paymentRepository.save(payment);

        log.info("Pago reportado exitosamente: {}", payment.getPaymentId());
        return convertToPaymentDTO(payment);
    }

    @Transactional
    public PaymentDTO reviewPayment(PaymentReviewDTO reviewDTO, Long adminUserId) {
        log.info("=== INICIANDO reviewPayment para paymentId: {} ===", reviewDTO.getPaymentId());

        Payment payment = paymentRepository.findById(reviewDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + reviewDTO.getPaymentId()));

        log.info(" Pago encontrado: ID={}, Loan={}, Amount={}, PaymentNumber={}",
                payment.getPaymentId(), payment.getLoan().getLoanId(),
                payment.getAmountPaid(), payment.getPaymentNumber());

        payment.setStatus(reviewDTO.getStatus());
        payment.setReviewComment(reviewDTO.getComment());
        payment.setReviewDate(LocalDateTime.now());
        payment.setReviewedBy(adminUserId);

        log.info(" Estado del pago actualizado a: {}", reviewDTO.getStatus());

        if ("APROBADO".equalsIgnoreCase(reviewDTO.getStatus()) ||
                "APPROVED".equalsIgnoreCase(reviewDTO.getStatus())) {

            Loan loan = payment.getLoan();
            BigDecimal amountPaid = payment.getAmountPaid();
            Integer paymentNumber = payment.getPaymentNumber();

            log.info("💰 Procesando APROBACIÓN del pago:");
            log.info("  - Préstamo ID: {}", loan.getLoanId());
            log.info("  - Balance actual: {}", loan.getBalance());
            log.info("  - Monto pagado: {}", amountPaid);
            log.info("  - Número de cuota: {}", paymentNumber);

            BigDecimal newBalance = loan.getBalance().subtract(amountPaid);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                newBalance = BigDecimal.ZERO;
            }

            log.info("  - Nuevo balance: {}", newBalance);
            loan.setBalance(newBalance);

            if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
                log.info("🎉 Préstamo COMPLETAMENTE PAGADO - Cambiando status a PAGADO");
                loan.setStatus("PAGADO");
            }

            loanRepository.save(loan);
            log.info(" Balance del préstamo actualizado exitosamente");

            try {
                LoanApplication loanApplication = loan.getLoanApplication();
                if (loanApplication != null) {
                    log.info(" Buscando cuota #{} en ProposedInstallments", paymentNumber);

                    List<ProposedInstallment> installments = proposedInstallmentRepository
                            .findByLoanApplication(loanApplication);

                    log.info("  - Total de cuotas encontradas: {}", installments.size());

                    Optional<ProposedInstallment> installmentOpt = installments.stream()
                            .filter(inst -> inst.getInstallmentNumber().equals(paymentNumber))
                            .findFirst();

                    if (installmentOpt.isPresent()) {
                        ProposedInstallment installment = installmentOpt.get();

                        log.info(" Cuota encontrada:");
                        log.info("  - Número: {}", installment.getInstallmentNumber());
                        log.info("  - Monto cuota: {}", installment.getAmount());
                        log.info("  - Estado actual: {}", installment.getStatus());
                        log.info("  - Monto pagado actual: {}", installment.getPaidAmount());

                        installment.setStatus("PAGADO");
                        installment.setPaidAmount(amountPaid);
                        installment.setPaidDate(LocalDateTime.now());

                        proposedInstallmentRepository.save(installment);

                        log.info(" Cuota #{} marcada como PAGADA con monto: {}",
                                paymentNumber, amountPaid);
                    } else {
                        log.warn(" No se encontró la cuota #{} para el préstamo {}",
                                paymentNumber, loan.getLoanId());
                    }
                } else {
                    log.warn(" LoanApplication no encontrado para el préstamo {}", loan.getLoanId());
                }
            } catch (Exception e) {
                log.error(" Error actualizando ProposedInstallment", e);
                // No lanzamos excepción para no revertir la transacción
            }
        } else {
            log.info(" Pago RECHAZADO - No se actualiza el balance ni las cuotas");
        }

        // 4. Guardar el pago actualizado
        Payment savedPayment = paymentRepository.save(payment);
        log.info("✅ Pago guardado exitosamente con nuevo estado: {}", savedPayment.getStatus());

        // 5. Convertir a DTO y retornar
        PaymentDTO dto = convertToDTO(savedPayment);

        log.info("=== FINALIZANDO reviewPayment ===");
        return dto;
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

    public List<PaymentDTO> getPendingPayments() {
        List<Payment> payments = paymentRepository.findByStatusOrderByPaymentDateDesc("PENDING");
        return payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getLoanPayments(Long loanId) {
        List<Payment> payments = paymentRepository.findByLoan_LoanIdOrderByPaymentDateDesc(loanId);
        return payments.stream()
                .map(this::convertToPaymentDTO)
                .collect(Collectors.toList());
    }

    public AccountStatementDTO getAccountStatement(Long loanId) {
        log.info("=== INICIANDO getAccountStatement para loanId: {} ===", loanId);

        try {
            Loan loan = loanRepository.findById(loanId)
                    .orElseThrow(() -> new RuntimeException("Préstamo no encontrado con ID: " + loanId));

            log.info(" Préstamo encontrado: ID={}, Status={}", loan.getLoanId(), loan.getStatus());

            if (loan.getLoanApplication() == null) {
                log.error(" El préstamo {} NO tiene LoanApplication asociada", loanId);
                throw new RuntimeException("El préstamo no tiene una solicitud asociada");
            }

            Long loanApplicationId = loan.getLoanApplication().getLoanApplicationID();
            log.info(" LoanApplication ID: {}", loanApplicationId);

            List<ProposedInstallment> proposedInstallments;
            try {
                proposedInstallments = proposedInstallmentRepository
                        .findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(loanApplicationId);
                log.info(" ProposedInstallments encontrados: {}", proposedInstallments.size());
            } catch (Exception e) {
                log.error(" Error obteniendo ProposedInstallments", e);
                throw new RuntimeException("Error al obtener el cronograma de cuotas propuestas: " + e.getMessage());
            }

            if (proposedInstallments.isEmpty()) {
                log.warn(" No hay cuotas propuestas para el préstamo {}", loanId);
                return buildBasicAccountStatement(loan);
            }

            List<Payment> payments = paymentRepository
                    .findByLoan_LoanIdOrderByPaymentDateDesc(loanId);
            log.info(" Pagos encontrados: {}", payments.size());

            AccountStatementDTO statement = new AccountStatementDTO();
            statement.setLoanId(loan.getLoanId());
            statement.setLoanAmount(loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO);

            BigDecimal loanAmount = loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO;

            BigDecimal totalAmount = proposedInstallments.stream()
                    .map(ProposedInstallment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalInterest = totalAmount.subtract(loanAmount);

            BigDecimal balance = loan.getBalance() != null ? loan.getBalance() : totalAmount;

            BigDecimal paidAmount = totalAmount.subtract(balance);

            statement.setTotalInterest(totalInterest);
            statement.setTotalAmount(totalAmount);
            statement.setBalance(balance);
            statement.setPaidAmount(paidAmount);
            statement.setStatus(loan.getStatus() != null ? loan.getStatus() : "ACTIVO");

            String itemName = "N/A";
            try {
                if (loan.getLoanApplication() != null &&
                        loan.getLoanApplication().getItem() != null &&
                        loan.getLoanApplication().getItem().getNameItem() != null) {
                    itemName = loan.getLoanApplication().getItem().getNameItem();
                }
            } catch (Exception e) {
                log.warn("⚠️ Error obteniendo nombre del item", e);
            }
            statement.setItemName(itemName);

            statement.setTotalPayments(proposedInstallments.size());

            long approvedPayments = payments.stream()
                    .filter(p -> "APPROVED".equals(p.getStatus()))
                    .count();
            statement.setPaidPayments((int) approvedPayments);

            List<PaymentScheduleDTO> scheduleDTOs = proposedInstallments.stream()
                    .map(this::convertProposedToScheduleDTO)
                    .collect(Collectors.toList());
            statement.setPaymentSchedule(scheduleDTOs);

            List<PaymentDTO> paymentDTOs = payments.stream()
                    .map(this::convertToPaymentDTO)
                    .collect(Collectors.toList());
            statement.setPayments(paymentDTOs);

            log.info(" Estado de cuenta construido exitosamente");
            log.info(" Total: {}, Interés: {}, Balance: {}, Pagado: {}",
                    totalAmount, totalInterest, balance, paidAmount);

            return statement;

        } catch (Exception e) {
            log.error(" ERROR CRÍTICO en getAccountStatement para loanId: {}", loanId, e);
            log.error(" Mensaje de error: {}", e.getMessage());
            throw new RuntimeException("Error al obtener el estado de cuenta: " + e.getMessage(), e);
        }
    }

    private AccountStatementDTO buildBasicAccountStatement(Loan loan) {
        log.info(" Construyendo estado de cuenta básico para préstamo {}", loan.getLoanId());

        AccountStatementDTO statement = new AccountStatementDTO();
        statement.setLoanId(loan.getLoanId());
        statement.setLoanAmount(loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO);
        statement.setTotalInterest(loan.getTotalInterest() != null ? loan.getTotalInterest() : BigDecimal.ZERO);
        statement.setTotalAmount(loan.getTotalAmount() != null ? loan.getTotalAmount() : BigDecimal.ZERO);
        statement.setBalance(loan.getBalance() != null ? loan.getBalance() : BigDecimal.ZERO);
        statement.setPaidAmount(BigDecimal.ZERO);
        statement.setStatus(loan.getStatus() != null ? loan.getStatus() : "ACTIVO");

        String itemName = "N/A";
        try {
            if (loan.getLoanApplication() != null &&
                    loan.getLoanApplication().getItem() != null) {
                itemName = loan.getLoanApplication().getItem().getNameItem();
            }
        } catch (Exception e) {
            log.warn("⚠ Error obteniendo nombre del item", e);
        }
        statement.setItemName(itemName);

        statement.setTotalPayments(loan.getTerm() != null ? loan.getTerm() : 0);
        statement.setPaidPayments(0);
        statement.setPaymentSchedule(List.of());
        statement.setPayments(List.of());

        return statement;
    }

    private void updatePaymentSchedule(Long loanId, Integer paymentNumber, BigDecimal amountPaid) {
        List<PaymentSchedule> schedules = paymentScheduleRepository
                .findByLoan_LoanIdOrderByPaymentNumberAsc(loanId);

        for (PaymentSchedule schedule : schedules) {
            if (schedule.getPaymentNumber().equals(paymentNumber)) {
                schedule.setStatus("PAID");
                schedule.setPaidAmount(amountPaid);
                schedule.setPaidDate(LocalDate.now());
                paymentScheduleRepository.save(schedule);
                break;
            }
        }
    }

    private PaymentDTO convertToPaymentDTO(Payment payment) {
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

        if (payment.getReference() != null) {
            dto.setReference(payment.getReference());
        }

        return dto;
    }

    private PaymentScheduleDTO convertProposedToScheduleDTO(ProposedInstallment installment) {
        PaymentScheduleDTO dto = new PaymentScheduleDTO();

        dto.setScheduleId(installment.getInstallmentId());
        dto.setPaymentNumber(installment.getInstallmentNumber());

        dto.setDueDate(installment.getDueDate() != null
                ? installment.getDueDate().format(DATETIME_FORMATTER)
                : null);

        dto.setAmountDue(installment.getAmount() != null ? installment.getAmount() : BigDecimal.ZERO);

        dto.setPrincipalAmount(BigDecimal.ZERO);
        dto.setInterestAmount(BigDecimal.ZERO);

        dto.setStatus("PENDIENTE");
        dto.setPaidAmount(null);
        dto.setPaidDate(null);

        return dto;
    }

    private PaymentScheduleDTO convertToScheduleDTO(PaymentSchedule schedule) {
        PaymentScheduleDTO dto = new PaymentScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setPaymentNumber(schedule.getPaymentNumber());

        dto.setDueDate(schedule.getDueDate() != null
                ? schedule.getDueDate().format(DATE_FORMATTER)
                : null);

        dto.setAmountDue(schedule.getAmountDue() != null ? schedule.getAmountDue() : BigDecimal.ZERO);
        dto.setPrincipalAmount(schedule.getPrincipalAmount() != null ? schedule.getPrincipalAmount() : BigDecimal.ZERO);
        dto.setInterestAmount(schedule.getInterestAmount() != null ? schedule.getInterestAmount() : BigDecimal.ZERO);
        dto.setStatus(schedule.getStatus() != null ? schedule.getStatus() : "PENDING");
        dto.setPaidAmount(schedule.getPaidAmount());

        dto.setPaidDate(schedule.getPaidDate() != null
                ? schedule.getPaidDate().format(DATE_FORMATTER)
                : null);

        return dto;
    }
}