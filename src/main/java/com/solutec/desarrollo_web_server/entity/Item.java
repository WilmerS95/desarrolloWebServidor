package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemID")
    private Integer itemID;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Column(name = "nameItem")
    private String nameItem;

    @Column(name = "brand")
    private String brand;

    @Column(name = "description")
    private String description;

    @Column(name = "specification")
    private String specification;

    @Column(name = "photos")
    private byte[] photos;
}