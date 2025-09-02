package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DocumentId")
    private Long documentId;

    @Column(name = "entityID")
    private Long entityID;

    @Column(name = "entityType")
    private String entityType;

    @Column(name = "filePath")
    private String filePath;

    @Column(name = "status")
    private String status;

    @Column(name = "uploadDate")
    private LocalDateTime uploadDate;

    @Column(name = "documentType")
    private String documentType;
}