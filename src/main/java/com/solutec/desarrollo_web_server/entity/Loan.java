package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loanId")
    private Long loanId;

    @ManyToOne
    @JoinColumn(name = "loanApplicationId")
    private LoanApplication loanApplication;

    @Column(name = "approvalDate")
    private LocalDateTime approvalDate;

    @Column(name = "agreement")
    private byte[] agreement;

    @Column(name = "loanAmount")
    private BigDecimal loanAmount;

    @Column(name = "interestRate")
    private BigDecimal interestRate;

    @Column(name = "term")
    private Integer term;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "status")
    private String status;

    @Column(name = "balance")
    private BigDecimal balance;
}