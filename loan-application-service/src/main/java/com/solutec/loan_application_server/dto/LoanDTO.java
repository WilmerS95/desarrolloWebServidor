package com.solutec.loan_application_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long loanId;
    private Long loanApplicationId;
    private String itemName;
    private String itemBrand;
    private LocalDateTime approvalDate;
    private LocalDateTime contractGeneratedDate;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer term;
    private LocalDateTime dueDate;
    private String status;
    private BigDecimal balance;
    private String contractNumber;
    private BigDecimal latePaymentFee;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private Integer gracePeriodDays;
    private Integer defaultDays;
}