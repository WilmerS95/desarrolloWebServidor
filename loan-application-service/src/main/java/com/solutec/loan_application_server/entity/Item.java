package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="itemId")
    private Long itemID;

    @Column(name="categoryId")
    private Long categoryId;

    @Column(name="nameItem")
    private String nameItem;

    @Column(name="brand")
    private String brand;

    @Column(name="description")
    private String description;

    @Column(name="specification")
    private String specification;
}
