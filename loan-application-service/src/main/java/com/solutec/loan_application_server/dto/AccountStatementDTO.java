package com.solutec.loan_application_server.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountStatementDTO {
    private Long loanId;
    private BigDecimal loanAmount;
    private BigDecimal totalInterest;
    private BigDecimal totalAmount;
    private BigDecimal balance;
    private BigDecimal paidAmount;
    private String status;
    private String itemName;
    private Integer totalPayments;
    private Integer paidPayments;
    private List<PaymentScheduleDTO> paymentSchedule;
    private List<PaymentDTO> payments;

    private String clientName;
    private String clientEmail;
    private String clientPhone;

}