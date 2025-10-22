package com.solutec.loan_application_server.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long loanId;
    private Integer paymentNumber;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String referenceBase64;
}
