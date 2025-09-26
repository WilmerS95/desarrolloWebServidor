package com.solutec.user_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    //@JsonIgnore
    private Permission permission;

    @Id
    @ManyToOne
    @JoinColumn(name = "roleId")
    //@JsonIgnore
    private Role role;
}