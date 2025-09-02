package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "UnredeemedItem")
public class UnredeemedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unredeemedItemID")
    private Long unredeemedItemID;

    @ManyToOne
    @JoinColumn(name = "loanID")
    private Loan loan;

    @Column(name = "availabilityDate")
    private LocalDateTime availabilityDate;

    @Column(name = "salePrice")
    private BigDecimal salePrice;

    @Column(name = "status")
    private String status;
}