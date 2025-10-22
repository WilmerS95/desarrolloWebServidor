package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationID")
    private Long notificationID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "sentDate")
    private LocalDateTime sentDate;

    @Column(name = "readStatus")
    private Boolean readStatus;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "relatedEntityType", length = 50)
    private String relatedEntityType;

    @Column(name = "relatedEntityId")
    private Long relatedEntityId;
}