package com.solutec.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {
    private String roleName;
    private String description;
    private List<Long> permissionIds;
}
