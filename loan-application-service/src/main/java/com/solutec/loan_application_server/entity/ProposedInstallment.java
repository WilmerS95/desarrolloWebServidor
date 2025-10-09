package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ProposedInstallment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposedInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long installmentId;

    @ManyToOne
    @JoinColumn(name = "loanApplicationId")
    private LoanApplication loanApplication;

    private Integer installmentNumber;
    private BigDecimal amount;
    private LocalDateTime dueDate;
}
