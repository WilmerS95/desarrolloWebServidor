package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ProcessState")
public class ProcessState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processStateID")
    private Integer processStateID;

    @Column(name = "stateName")
    private String stateName;

    @Column(name = "description")
    private String description;

    @Column(name = "registrationDate")
    private LocalDateTime registrationDate;
}