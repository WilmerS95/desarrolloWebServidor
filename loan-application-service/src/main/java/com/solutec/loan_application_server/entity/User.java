package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "User")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID")
    private Long userID;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "firstLastName", nullable = false)
    private String firstLastName;

    @Column(name = "secondLastName")
    private String secondLastName;

    @Column(name = "marriedLastName")
    private String marriedLastName;

    @Column(name = "secondOrMoreNames")
    private String secondOrMoreNames;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "address")
    private String address;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleID", referencedColumnName = "roleID")
    private Role role;

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles = new HashSet<>();*/
}