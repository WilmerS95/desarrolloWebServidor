package com.solutec.loan_application_server.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LoanApplicationResponse(
        Long loanApplicationId,
        Long itemId,
        String itemName,
        String brand,
        Integer quantityPayments,
        LocalDateTime applicationDate,
        String status,
        List<String> photoUrls
) {}
