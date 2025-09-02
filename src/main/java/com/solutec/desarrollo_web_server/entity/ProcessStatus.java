package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ProcessStatus")
public class ProcessStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processStatusID")
    private Integer processStatusID;

    @ManyToOne
    @JoinColumn(name = "currentStateID")
    private ProcessState currentState;

    @Column(name = "entityType")
    private String entityType;

    @Column(name = "entityID")
    private Integer entityID;

    @Column(name = "lastUpdated")
    private LocalDateTime lastUpdated;
}