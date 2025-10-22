package com.solutec.loan_application_server.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleDTO {
    private Long scheduleId;
    private Integer paymentNumber;
    private String dueDate;
    private BigDecimal amountDue;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private String status;
    private BigDecimal paidAmount;
    private String paidDate;
}