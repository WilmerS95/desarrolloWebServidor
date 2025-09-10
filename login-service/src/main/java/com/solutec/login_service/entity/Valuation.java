/*
package com.solutec.login_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Valuation")
public class Valuation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "valuationID")
    private Long valuationID;

    @ManyToOne
    @JoinColumn(name = "loanApplicationID")
    private LoanApplication loanApplication;

    @Column(name = "appraiserID")
    private Long appraiserID;

    @Column(name = "valuationDate")
    private LocalDateTime valuationDate;

    @Column(name = "estimatedValue")
    private BigDecimal estimatedValue;

    @Column(name = "comments")
    private String comments;
}*/
