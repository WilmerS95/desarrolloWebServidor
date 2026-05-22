package com.solutec.competition_service.entity;

import com.solutec.competition_service.entity.enums.CardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardID")
    private Long cardID;

    @ManyToOne
    @JoinColumn(name = "matchID", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "playerID", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CardType type;

    @Column(name = "minute", nullable = false)
    private Integer minute;

    @Column(name = "reason")
    private String reason;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}