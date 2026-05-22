package com.solutec.competition_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goalID")
    private Long goalID;

    @ManyToOne
    @JoinColumn(name = "matchID", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "scorerID", nullable = false)
    private Player scorer;

    @ManyToOne
    @JoinColumn(name = "assistID")
    private Player assist;

    @Column(name = "minute", nullable = false)
    private Integer minute;

    @Column(name = "extraMinute")
    private Integer extraMinute; // Minuto extra (92+2, etc.)

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}