/*
package com.solutec.login_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "AuditLog")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auditID")
    private Long auditID;

    @ManyToOne
    @JoinColumn(name = "changedBy")
    private User changedBy;

    @Column(name = "entityType")
    private String entityType;

    @Column(name = "entityID")
    private Long entityID;

    @Column(name = "action")
    private String action;

    @Column(name = "oldState")
    private String oldState;

    @Column(name = "newState")
    private String newState;

    @Column(name = "changeDate")
    private LocalDateTime changeDate;
}*/
