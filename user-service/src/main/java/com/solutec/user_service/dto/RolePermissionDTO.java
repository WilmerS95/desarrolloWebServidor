package com.solutec.user_service.dto;

import lombok.*;

@Data
public class RolePermissionDTO {
    private Long permissionId;
    private String permissionName;

    public RolePermissionDTO(Long permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }
}