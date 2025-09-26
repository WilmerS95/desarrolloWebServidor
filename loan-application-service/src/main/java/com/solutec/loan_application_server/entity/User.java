package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID")
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

    @Column(name = "firstLastName", nullable = false)
    private String firstLastName;

    @Column(name = "secondLastName")
    private String secondLastName;

    @Column(name = "marriedLastName")
    private String marriedLastName;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "address")
    private String address;

    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleID")
    private Role role;*/

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles = new HashSet<>();*/
}
