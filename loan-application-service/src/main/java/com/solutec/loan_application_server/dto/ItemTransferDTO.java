package com.solutec.loan_application_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTransferDTO {
    private Long itemId;
    private Long loanId;
    private BigDecimal salePrice;
    private String reason; // VENCIMIENTO, MANUAL, INCUMPLIMIENTO
    private String adminComment;
    private Long transferredBy; // userId del admin que lo transfiere
}




