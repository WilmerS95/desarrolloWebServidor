package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "loanID")
    private Loan loan;

    @Column(name = "quantityPayments")
    private Integer quantityPayments;

    @Column(name = "paymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "amountPaid")
    private BigDecimal amountPaid;

    @Column(name = "reference")
    private byte[] reference;

    @Column(name = "paymentMethod")
    private String paymentMethod;
}