package com.solutec.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Role")
public class Role {

    @Id
    @Column(name = "roleId")
    private Long roleId;

    @Column(name = "roleName", nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> rolePermissions;
}