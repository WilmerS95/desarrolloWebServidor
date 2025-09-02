package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "StorageCost")
public class StorageCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storageCostID")
    private Integer storageCostID;

    @ManyToOne
    @JoinColumn(name = "itemID")
    private Item item;

    @Column(name = "dailyRate")
    private BigDecimal dailyRate;

    @Column(name = "accumulatedCost")
    private BigDecimal accumulatedCost;

    @Column(name = "calculationDate")
    private LocalDateTime calculationDate;
}