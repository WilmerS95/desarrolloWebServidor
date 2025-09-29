package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loanApplicationID")
    private Long loanApplicationID;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @OneToOne
    @JoinColumn(name = "itemID", nullable = false)
    private Item item;

    @Column(name = "quantityPayments")
    private Integer quantityPayments;

    @Column(name = "applicationDate")
    private java.time.LocalDateTime applicationDate;

    @Column(name = "status")
    private String status;
}
