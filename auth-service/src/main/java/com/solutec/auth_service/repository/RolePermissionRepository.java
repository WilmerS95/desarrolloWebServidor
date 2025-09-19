package com.solutec.auth_service.repository;

import com.solutec.auth_service.entity.RolePermission;
import com.solutec.auth_service.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
}