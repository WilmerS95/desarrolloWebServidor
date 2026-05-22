package com.solutec.competition_service.entity;

import com.solutec.competition_service.entity.enums.PlayerPosition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "Player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playerID")
    private Long playerID;

    @ManyToOne
    @JoinColumn(name = "teamID", nullable = false)
    private Team team;

    @Column(name = "userID")
    private Long userID; // Referencia al usuario del sistema (para árbitros, delegados, etc.)

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastNameFirst", nullable = false)
    private String lastNameFirst;

    @Column(name = "lastNameSecond")
    private String lastNameSecond;

    @Column(name = "shirtNumber")
    private Integer shirtNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private PlayerPosition position;

    @Column(name = "birthDate")
    private LocalDate birthDate;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "dpi", unique = true)
    private String dpi;

    @Column(name = "photoUrl")
    private String photoUrl;

    @Column(name = "registrationDate")
    private LocalDateTime registrationDate;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
}