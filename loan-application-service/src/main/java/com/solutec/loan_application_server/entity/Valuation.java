package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "valuation")
public class Valuation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "valuationID")
    private Integer valuationID;

    @ManyToOne
    @JoinColumn(name = "loanApplicationID", nullable = false)
    private LoanApplication loanApplication;

    @Column(name = "appraiserID")
    private Integer appraiserID;

    @Column(name = "valuationDate")
    private LocalDateTime valuationDate;

    @Column(name = "estimatedValue", precision = 18, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "comments", length = 500)
    private String comments;
}
