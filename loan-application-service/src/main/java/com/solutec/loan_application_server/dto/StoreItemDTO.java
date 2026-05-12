package com.solutec.loan_application_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreItemDTO {
    private Long itemId;
    private String nameItem;
    private String brand;
    private String description;
    private String specification;
    private String photos;
    private BigDecimal salePrice;
    private BigDecimal originalLoanAmount;
    private String categoryName;
    private Long categoryId;
    private String transferReason;
    private LocalDateTime availableSince;
    private String itemStatus; // AVAILABLE, SOLD, RESERVED
    private Boolean isSold;
}
