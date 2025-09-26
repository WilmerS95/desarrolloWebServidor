package com.solutec.user_service.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Long permissionId;
    private String permissionName;

    public PermissionDTO(Long permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }
}
