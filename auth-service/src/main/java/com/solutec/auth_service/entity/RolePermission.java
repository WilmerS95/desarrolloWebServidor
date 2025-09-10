package com.solutec.login_service.entity;

import com.solutec.auth_service.entity.Permission;
import com.solutec.auth_service.entity.Role;
import com.solutec.auth_service.entity.RolePermissionId;
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