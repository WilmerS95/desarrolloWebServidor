package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "DebtRecovery")
public class DebtRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recoveryID")
    private Long recoveryID;

    @ManyToOne
    @JoinColumn(name = "loanID")
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    @Column(name = "visitDate")
    private LocalDateTime visitDate;

    @Column(name = "result")
    private String result;

    @Column(name = "comments")
    private String comments;
}