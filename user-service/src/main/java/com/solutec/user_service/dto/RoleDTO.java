package com.solutec.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    private Long roleId;
    private String roleName;
    private String description;
    private List<RolePermissionDTO> permissions;
}
