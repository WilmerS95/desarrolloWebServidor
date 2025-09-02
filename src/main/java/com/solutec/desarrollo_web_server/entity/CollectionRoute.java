package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CollectionRoute")
public class CollectionRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routeId")
    private Long routeId;

    @ManyToOne
    @JoinColumn(name = "collectorID")
    private Collector collector;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "status")
    private String status;

    @Column(name = "notes")
    private String notes;
}