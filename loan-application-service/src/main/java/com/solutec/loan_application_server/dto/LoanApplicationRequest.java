package com.solutec.loan_application_server.dto;

import lombok.*;

@Getter
@Setter
@Data
public class LoanApplicationRequest {
    private String nameItem;
    private String brand;
    private String description;
    private String specification;
    private Integer quantityPayments;
    private Long categoryId;
}