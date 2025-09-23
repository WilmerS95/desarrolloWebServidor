package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="categoryId")
    private Long categoryId;

    @Column(name="categoryName")
    private String categoryName;

    @Column(name="description")
    private String description;

    @Column(name="percentage")
    private Float percentage;
}
