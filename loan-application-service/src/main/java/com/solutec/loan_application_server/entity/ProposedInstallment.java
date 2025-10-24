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
@Getter
@Setter
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

    @Column(name = "status", length = 50)
    private String status = "PENDIENTE";

    @Column(name = "paidAmount", precision = 18, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "paidDate")
    private LocalDateTime paidDate;
}
