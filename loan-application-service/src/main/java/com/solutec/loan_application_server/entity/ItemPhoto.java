package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class ItemPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemPhotoId;

    @ManyToOne
    @JoinColumn(name = "itemId", referencedColumnName = "itemId")
    private Item item;

    @Column(name = "photoPath")
    private String photoPath;
}
