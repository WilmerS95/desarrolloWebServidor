package com.solutec.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditID;

    @ManyToOne
    @JoinColumn(name = "changedBy")
    private User changedBy;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityID;

    @Column(nullable = false)
    private String action;

    @Column(length = 500)
    private String oldState;

    @Column(length = 500)
    private String newState;

    @Column(nullable = false)
    private LocalDateTime changeDate;
}
