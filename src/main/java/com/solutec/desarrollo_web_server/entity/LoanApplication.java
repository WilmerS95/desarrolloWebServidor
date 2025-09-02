package com.solutec.desarrollo_web_server.entity;

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
    private Integer loanApplicationID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @Column(name = "itemID")
    private Integer itemID;

    @Column(name = "quantityPayments")
    private Integer quantityPayments;

    @Column(name = "applicationDate")
    private LocalDateTime applicationDate;

    @Column(name = "status")
    private String status;
}