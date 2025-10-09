package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @OneToOne
    @JoinColumn(name = "loanApplicationId", nullable = false)
    private LoanApplication loanApplication;

    @Column(name = "approvalDate")
    private LocalDateTime approvalDate;

    @Lob
    @Column(name = "agreement")
    private byte[] agreement;

    @Column(name = "loanAmount", precision = 18, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "interestRate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "term")
    private Integer term;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "balance", precision = 18, scale = 2)
    private BigDecimal balance;
}
