package com.solutec.loan_application_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemForTransferDTO {
    private Long itemId;
    private Long loanId;
    private String itemName;
    private String brand;
    private String categoryName;
    private BigDecimal loanAmount;
    private BigDecimal balance;
    private String clientName;
    private String loanStatus;
    private Boolean hasOverduePayments;
    private Integer daysOverdue;
}