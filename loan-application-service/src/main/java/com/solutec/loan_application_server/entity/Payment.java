package com.solutec.loan_application_server.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanID", nullable = false)
    private Loan loan;

    @Column(name = "paymentNumber")
    private Integer paymentNumber;

    @Column(name = "paymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "amountPaid", precision = 18, scale = 2)
    private BigDecimal amountPaid;

    @Lob
    @Column(name = "reference", columnDefinition = "LONGBLOB")
    private byte[] reference;

    @Column(name = "paymentMethod", length = 200)
    private String paymentMethod;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reviewedBy")
    private Long reviewedBy;

    @Column(name = "reviewDate")
    private LocalDateTime reviewDate;

    @Column(name = "reviewComment", length = 500)
    private String reviewComment;
}