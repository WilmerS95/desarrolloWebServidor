package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ProcessTransition")
public class ProcessTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transitionID")
    private Integer transitionID;

    @ManyToOne
    @JoinColumn(name = "fromStateID")
    private ProcessState fromState;

    @ManyToOne
    @JoinColumn(name = "toStateID")
    private ProcessState toState;

    @Column(name = "transitionDate")
    private LocalDateTime transitionDate;

    @Column(name = "comment")
    private String comment;
}