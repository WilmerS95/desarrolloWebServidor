package com.solutec.loan_application_server.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private Long loanId;
    private Integer paymentNumber;
    private LocalDateTime paymentDate;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String status;
    private String reviewComment;
    private LocalDateTime reviewDate;
    private String reviewedByUsername;
    private byte[] reference;
}