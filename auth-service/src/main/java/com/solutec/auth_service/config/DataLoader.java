package com.solutec.auth_service.config;

import com.solutec.auth_service.entity.*;
import com.solutec.auth_service.repository.PermissionRepository;
import com.solutec.auth_service.repository.RolePermissionRepository;
import com.solutec.auth_service.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @PostConstruct
    @Transactional
    public void loadInitialData() {
        // Permisos
        Permission viewItems = createPermissionIfNotExists("VIEW_ITEMS");
        Permission createOrder = createPermissionIfNotExists("CREATE_ORDER");
        Permission viewOwnLoans = createPermissionIfNotExists("VIEW_OWN_LOANS");
        Permission uploadDocument = createPermissionIfNotExists("UPLOAD_DOCUMENT");

        Permission manageUsers = createPermissionIfNotExists("MANAGE_USERS");
        Permission manageItems = createPermissionIfNotExists("MANAGE_ITEMS");
        Permission approveLoans = createPermissionIfNotExists("APPROVE_LOANS");
        Permission viewAllOrders = createPermissionIfNotExists("VIEW_ALL_ORDERS");
        Permission managePromotions = createPermissionIfNotExists("MANAGE_PROMOTIONS");
        Permission allPermission = createPermissionIfNotExists("ALL_PERMISSION");

        Role clienteRole = createRoleIfNotExists("CLIENTE");
        Role adminRole = createRoleIfNotExists("ADMIN");
        Role saRole = createRoleIfNotExists("SA");

        assignPermissionsToRole(clienteRole, Set.of(viewItems, createOrder, viewOwnLoans, uploadDocument));
        assignPermissionsToRole(adminRole, Set.of(viewItems, createOrder, viewOwnLoans, uploadDocument,
                manageUsers, manageItems, approveLoans, viewAllOrders, managePromotions));
        assignPermissionsToRole(saRole, new HashSet<>(permissionRepository.findAll()));
    }

    private Permission createPermissionIfNotExists(String name) {
        return permissionRepository.findByPermissionName(name)
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setPermissionName(name);
                    return permissionRepository.save(p);
                });
    }

    private Role createRoleIfNotExists(String name) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(name);
                    return roleRepository.save(role);
                });
    }

    private void assignPermissionsToRole(Role role, Set<Permission> permissions) {
        for (Permission p : permissions) {
            boolean exists = rolePermissionRepository.findAll().stream()
                    .anyMatch(rp -> rp.getRole().getRoleId().equals(role.getRoleId())
                            && rp.getPermission().getPermissionId().equals(p.getPermissionId()));

            if (!exists) {
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            }
        }
    }
}