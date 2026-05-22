package com.solutec.competition_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Standing")
public class Standing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standingID")
    private Long standingID;

    @ManyToOne
    @JoinColumn(name = "groupID", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "teamID", nullable = false)
    private Team team;

    @Column(name = "position")
    private Integer position;

    @Column(name = "matches")
    private Integer matches = 0;

    @Column(name = "wins")
    private Integer wins = 0;

    @Column(name = "draws")
    private Integer draws = 0;

    @Column(name = "losses")
    private Integer losses = 0;

    @Column(name = "goalsFor")
    private Integer goalsFor = 0;

    @Column(name = "goalsAgainst")
    private Integer goalsAgainst = 0;

    @Column(name = "goalDifference")
    private Integer goalDifference = 0;

    @Column(name = "points")
    private Integer points = 0;

    public void updatePoints() {
        this.points = (wins * 3) + draws;
        this.goalDifference = goalsFor - goalsAgainst;
        this.matches = wins + draws + losses;
    }
}