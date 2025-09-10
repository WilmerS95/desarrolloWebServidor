package com.solutec.login_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "`Order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderID")
    private Long orderID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @Column(name = "orderDate")
    private LocalDateTime orderDate;

    @Column(name = "totalAmount")
    private BigDecimal totalAmount;

    @Column(name = "status")
    private String status;
}