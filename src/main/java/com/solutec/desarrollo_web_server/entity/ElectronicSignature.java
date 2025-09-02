package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ElectronicSignature")
public class ElectronicSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signatureID")
    private Long signatureID;

    @ManyToOne
    @JoinColumn(name = "documentID")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;

    @Column(name = "signedDate")
    private LocalDateTime signedDate;

    @Column(name = "status")
    private String status;

    @Column(name = "signatureHash")
    private String signatureHash;
}