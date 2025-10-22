package com.solutec.loan_application_server.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReviewDTO {
    private Long paymentId;
    private String status; // APROBADO o RECHAZADO
    private String comment;
}