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
        Payment payment = paymentRepository.findById(reviewDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        payment.setStatus(reviewDTO.getStatus());
        payment.setReviewComment(reviewDTO.getComment());
        payment.setReviewDate(LocalDateTime.now());
        payment.setReviewedBy(adminUserId);

        if ("APPROVED".equals(reviewDTO.getStatus())) {
            Loan loan = payment.getLoan();

            BigDecimal currentBalance = loan.getBalance() != null ? loan.getBalance() : BigDecimal.ZERO;
            BigDecimal newBalance = currentBalance.subtract(payment.getAmountPaid());
            loan.setBalance(newBalance.max(BigDecimal.ZERO));

            if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                loan.setStatus("PAID");
            }

            loanRepository.save(loan);

            updatePaymentSchedule(loan.getLoanId(), payment.getPaymentNumber(), payment.getAmountPaid());
        }

        payment = paymentRepository.save(payment);
        log.info("Pago {} revisado como {}", payment.getPaymentId(), payment.getStatus());

        return convertToPaymentDTO(payment);
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
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        List<ProposedInstallment> proposedInstallments = proposedInstallmentRepository
                .findByLoanApplicationLoanApplicationIDOrderByInstallmentNumber(
                        loan.getLoanApplication().getLoanApplicationID()
                );

        List<Payment> payments = paymentRepository
                .findByLoan_LoanIdOrderByPaymentDateDesc(loanId);

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
        statement.setStatus(loan.getStatus() != null ? loan.getStatus() : "ACTIVE");

        if (loan.getLoanApplication() != null && loan.getLoanApplication().getItem() != null) {
            statement.setItemName(loan.getLoanApplication().getItem().getNameItem());
        } else {
            statement.setItemName("N/A");
        }

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

        log.info("📊 Estado de cuenta - Total: {}, Interés: {}, Balance: {}, Pagado: {}",
                totalAmount, totalInterest, balance, paidAmount);

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