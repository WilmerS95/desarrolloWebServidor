package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.client.BusinessParameterClient;
import com.solutec.loan_application_server.entity.ProposedInstallment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InstallmentCalculationService {

    @Autowired
    private BusinessParameterClient parameterClient;

    public List<ProposedInstallment> calculateInstallments(
            Double principal,
            Integer numberOfInstallments) {

        List<ProposedInstallment> installments = new ArrayList<>();
        BigDecimal principalAmount = BigDecimal.valueOf(principal);

        BigDecimal monthlyRate = getMonthlyInterestRate();

        BigDecimal totalInterest = principalAmount
                .multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(numberOfInstallments));

        BigDecimal totalAmount = principalAmount.add(totalInterest);

        BigDecimal installmentAmount = totalAmount
                .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);

        LocalDateTime baseDate = LocalDateTime.now();
        for (int i = 1; i <= numberOfInstallments; i++) {
            ProposedInstallment installment = new ProposedInstallment();
            installment.setInstallmentNumber(i);
            installment.setAmount(installmentAmount);
            installment.setDueDate(baseDate.plusDays(30L * i));
            installments.add(installment);
        }

        return installments;
    }

    private BigDecimal getMonthlyInterestRate() {
        try {
            Map<String, String> response = parameterClient.getParameterValue("INTEREST_RATE_MONTHLY");
            String value = response.get("value");
            if (value != null) {
                return new BigDecimal(value).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.error("Error fetching interest rate from parameter service, using default", e);
        }
        return new BigDecimal("0.05"); // 5%
    }

    public BigDecimal getTotalAmount(List<ProposedInstallment> installments) {
        return installments.stream()
                .map(ProposedInstallment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}