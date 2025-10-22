package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentId")
    private Integer paymentId;

    @Column(name = "loanID")
    private Integer loanID;

    @Column(name = "quantityPayments")
    private Integer quantityPayments;

    @Column(name = "paymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "amountPaid", precision = 18, scale = 2)
    private BigDecimal amountPaid;

    @Lob
    @Column(name = "reference")
    private byte[] reference;

    @Column(name = "paymentMethod", length = 200)
    private String paymentMethod;
}