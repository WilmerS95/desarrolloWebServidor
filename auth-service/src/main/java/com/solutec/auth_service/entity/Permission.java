package com.solutec.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Permission")
public class Permission {

    @Id
    @Column(name = "permissionId")
    private Long permissionId;

    @Column(name = "permissionName", nullable = false)
    private String permissionName;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> rolePermissions;
}