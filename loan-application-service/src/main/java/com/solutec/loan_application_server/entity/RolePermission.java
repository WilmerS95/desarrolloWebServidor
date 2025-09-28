package com.solutec.loan_application_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "RolePermission")
@IdClass(RolePermissionId.class)
public class RolePermission {

    @Id
    @ManyToOne
    @JoinColumn(name = "permissionID")
    private Permission permission;

    @Id
    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;
}