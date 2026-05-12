package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "item_availability")
public class ItemAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityId;

    @OneToOne
    @JoinColumn(name = "itemId", referencedColumnName = "itemId", unique = true)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "loanId", referencedColumnName = "loanId")
    private Loan originalLoan;

    @Column(name = "sale_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal salePrice;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, SOLD, RESERVED

    @Column(name = "transfer_reason", nullable = false, length = 50)
    private String transferReason; // VENCIMIENTO, MANUAL, INCUMPLIMIENTO

    @Column(name = "transferred_by")
    private Long transferredBy; // userId del admin

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "available_since", nullable = false)
    private LocalDateTime availableSince;

    @Column(name = "sold_date")
    private LocalDateTime soldDate;

    @Column(name = "sold_to")
    private Long soldTo; // userId del comprador

    @Column(name = "sale_amount", precision = 10, scale = 2)
    private BigDecimal saleAmount;

    @PrePersist
    protected void onCreate() {
        if (availableSince == null) {
            availableSince = LocalDateTime.now();
        }
        if (status == null) {
            status = "AVAILABLE";
        }
    }
}