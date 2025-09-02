package com.solutec.desarrollo_web_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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