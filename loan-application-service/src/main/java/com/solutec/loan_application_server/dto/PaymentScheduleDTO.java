package com.solutec.loan_application_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentScheduleDTO {
    private Long scheduleId;
    private Integer paymentNumber;
    private LocalDateTime dueDate;
    private BigDecimal amountDue;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private String status;

    private BigDecimal paidAmount;
    private LocalDateTime paidDate;

    public PaymentScheduleDTO() {}

}