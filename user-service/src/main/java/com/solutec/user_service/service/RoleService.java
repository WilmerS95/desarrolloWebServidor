package com.solutec.user_service.service;

import com.solutec.user_service.dto.RoleDTO;
import com.solutec.user_service.dto.RolePermissionDTO;
import com.solutec.user_service.entity.Permission;
import com.solutec.user_service.entity.Role;
import com.solutec.user_service.entity.RolePermission;
import com.solutec.user_service.repository.RoleRepository;
import com.solutec.user_service.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return toDTO(role);
    }

    @Transactional
    public RoleDTO createRole(Role role, List<Long> permissionIds) {
        Set<RolePermission> perms = permissionIds.stream()
                .map(pid -> {
                    Permission p = permissionRepository.findById(pid)
                            .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + pid));
                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(p);
                    return rp;
                }).collect(Collectors.toSet());

        role.setRolePermissions(perms);
        Role saved = roleRepository.save(role);
        return toDTO(saved);
    }


    @Transactional
    public RoleDTO updateRole(Long id, Role roleData, List<Long> permissionIds) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        role.setRoleName(roleData.getRoleName());
        role.setDescription(roleData.getDescription());

        // Actualizar permisos
        role.getRolePermissions().clear();
        Set<RolePermission> newPerms = permissionIds.stream()
                .map(pid -> {
                    Permission p = permissionRepository.findById(pid)
                            .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + pid));
                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(p);
                    return rp;
                }).collect(Collectors.toSet());

        role.setRolePermissions(newPerms);

        Role updated = roleRepository.save(role);
        return toDTO(updated);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    private RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());

        List<RolePermissionDTO> permissions = role.getRolePermissions().stream()
                .map(rp -> {
                    Permission p = rp.getPermission();
                    return new RolePermissionDTO(
                            p.getPermissionId(),
                            p.getPermissionName()
                    );
                })
                .toList();

        dto.setPermissions(permissions);
        return dto;
    }

}
