package com.solutec.competition_service.entity;

import com.solutec.competition_service.entity.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "Matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matchID")
    private Long matchID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "groupID")
    private Group group; // Null si es fase de eliminatoria

    @ManyToOne
    @JoinColumn(name = "homeTeamID", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "awayTeamID", nullable = false)
    private Team awayTeam;

    @Column(name = "matchDate", nullable = false)
    private LocalDateTime matchDate;

    @Column(name = "stadium")
    private String stadium;

    @Column(name = "refereeUserID")
    private Long refereeUserID; // ID del árbitro principal

    @Column(name = "homeGoals")
    private Integer homeGoals = 0;

    @Column(name = "awayGoals")
    private Integer awayGoals = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Goal> goals;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Card> cards;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = MatchStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}