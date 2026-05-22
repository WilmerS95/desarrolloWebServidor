package com.solutec.competition_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "Team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teamID")
    private Long teamID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", nullable = false)
    private Tournament tournament;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "acronym")
    private String acronym;

    @Column(name = "city")
    private String city;

    @Column(name = "foundedDate")
    private LocalDate foundedDate;

    @Column(name = "photoUrl")
    private String photoUrl;

    @Column(name = "shirtColor")
    private String shirtColor;

    @Column(name = "registrationDate")
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Player> players;

    @ManyToMany
    @JoinTable(
            name = "team_group",
            joinColumns = @JoinColumn(name = "teamID"),
            inverseJoinColumns = @JoinColumn(name = "groupID")
    )
    private Set<Group> groups;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
}