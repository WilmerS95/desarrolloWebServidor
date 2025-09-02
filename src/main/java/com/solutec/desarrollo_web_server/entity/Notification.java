package com.solutec.desarrollo_web_server.entity;

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
    private Integer notificationID;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "message")
    private String message;

    @Column(name = "sentDate")
    private LocalDateTime sentDate;

    @Column(name = "readStatus")
    private Boolean readStatus;

    @Column(name = "type")
    private String type;
}