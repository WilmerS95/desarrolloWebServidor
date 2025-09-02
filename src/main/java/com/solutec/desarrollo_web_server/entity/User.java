package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "User")
public class User {

    @Id
    @Column(name = "userID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "secondOrMoreNames")
    private String secondOrMoreNames;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "secondLastName")
    private String secondLastName;

    @Column(name = "marriedLastName")
    private String marriedLastName;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
}
