package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RouteAssignment")
public class RouteAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignmentID")
    private Long assignmentID;

    @ManyToOne
    @JoinColumn(name = "routeId")
    private CollectionRoute route;

    @ManyToOne
    @JoinColumn(name = "loanId")
    private Loan loan;
}