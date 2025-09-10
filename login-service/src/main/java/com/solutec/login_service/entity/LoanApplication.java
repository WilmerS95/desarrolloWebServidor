/*
package com.solutec.login_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "LoanApplication")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loanApplicationID")
    private Long loanApplicationID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @Column(name = "itemID")
    private Long itemID;

    @Column(name = "quantityPayments")
    private Integer quantityPayments;

    @Column(name = "applicationDate")
    private LocalDateTime applicationDate;

    @Column(name = "status")
    private String status;
}*/
