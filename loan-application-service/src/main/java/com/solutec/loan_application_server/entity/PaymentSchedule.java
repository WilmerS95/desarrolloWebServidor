package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "PaymentSchedule")
public class PaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduleId")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loanID", nullable = false)
    private Loan loan;

    @Column(name = "paymentNumber")
    private Integer paymentNumber;

    @Column(name = "dueDate")
    private LocalDate dueDate;

    @Column(name = "amountDue", precision = 18, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "principalAmount", precision = 18, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interestAmount", precision = 18, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "paidAmount", precision = 18, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "paidDate")
    private LocalDate paidDate;

    @Column(name = "notificationSent")
    private Boolean notificationSent;

    @Column(name = "collectorNotified")
    private Boolean collectorNotified;
}