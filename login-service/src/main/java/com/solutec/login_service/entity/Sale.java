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
@Table(name = "Sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saleID")
    private Long saleID;

    @ManyToOne
    @JoinColumn(name = "unredeemedItemID")
    private UnredeemedItem unredeemedItem;

    @Column(name = "saleDate")
    private LocalDateTime saleDate;

    @Column(name = "saleAmount")
    private BigDecimal saleAmount;
}*/
