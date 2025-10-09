package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.entity.ProposedInstallment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstallmentCalculationService {

    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.05");

    public List<ProposedInstallment> calculateInstallments(Double principal, Integer numberOfPayments) {
        BigDecimal amount = BigDecimal.valueOf(principal);
        BigDecimal totalInterest = amount.multiply(INTEREST_RATE)
                .multiply(BigDecimal.valueOf(numberOfPayments));
        BigDecimal totalAmount = amount.add(totalInterest);
        BigDecimal installmentAmount = totalAmount.divide(
                BigDecimal.valueOf(numberOfPayments),
                2,
                RoundingMode.HALF_UP
        );

        List<ProposedInstallment> installments = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int dayOfMonth = today.getDayOfMonth();

        for (int i = 1; i <= numberOfPayments; i++) {
            ProposedInstallment installment = new ProposedInstallment();
            installment.setInstallmentNumber(i);
            installment.setAmount(installmentAmount);

            LocalDate dueDate = calculateDueDate(today, i, dayOfMonth);
            installment.setDueDate(dueDate.atStartOfDay());

            installments.add(installment);
        }

        return installments;
    }

    private LocalDate calculateDueDate(LocalDate startDate, int monthsToAdd, int preferredDayOfMonth) {
        LocalDate targetDate = startDate.plusMonths(monthsToAdd);
        int lastDayOfMonth = targetDate.lengthOfMonth();

        if (preferredDayOfMonth <= lastDayOfMonth) {
            return targetDate.withDayOfMonth(preferredDayOfMonth);
        } else {
            return targetDate.plusMonths(1).withDayOfMonth(1);
        }
    }

    public BigDecimal getTotalAmount(List<ProposedInstallment> installments) {
        return installments.stream()
                .map(ProposedInstallment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}