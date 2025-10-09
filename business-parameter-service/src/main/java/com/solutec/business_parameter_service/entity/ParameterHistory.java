package com.solutec.business_parameter_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ParameterHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    private Integer parameterId;

    @Column(nullable = false, length = 50)
    private String parameterName;

    @Column(length = 200)
    private String oldValue;

    @Column(length = 200)
    private String newValue;

    @Column(length = 200)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(length = 50)
    private String action;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}