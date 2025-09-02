package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Integer taskId;

    @ManyToOne
    @JoinColumn(name = "assignedTo")
    private User assignedTo;

    @Column(name = "description")
    private String description;

    @Column(name = "priority")
    private String priority;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "status")
    private String status;

    @Column(name = "relatedEntityID")
    private Integer relatedEntityID;

    @Column(name = "entityType")
    private String entityType;
}