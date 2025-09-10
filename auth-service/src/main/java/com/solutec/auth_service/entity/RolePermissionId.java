package com.solutec.auth_service.entity;

import java.io.Serializable;
import java.util.Objects;

public class RolePermissionId implements Serializable {

    private Long permission;
    private Long role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(permission, that.permission) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, role);
    }
}