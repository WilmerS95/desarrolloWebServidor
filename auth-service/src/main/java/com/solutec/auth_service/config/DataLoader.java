package com.solutec.auth_service.config;

import com.solutec.auth_service.entity.*;
import com.solutec.auth_service.repository.PermissionRepository;
import com.solutec.auth_service.repository.RolePermissionRepository;
import com.solutec.auth_service.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @PostConstruct
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

        // Crear roles
        Role clienteRole = createRoleIfNotExists(2L, "CLIENTE");
        Role adminRole = createRoleIfNotExists(1L, "ADMIN");
        Role saRole = createRoleIfNotExists(3L, "SA");

        assignPermissionsToRole(clienteRole, Set.of(viewItems, createOrder, viewOwnLoans, uploadDocument));
        assignPermissionsToRole(adminRole, Set.of(viewItems, createOrder, viewOwnLoans, uploadDocument,
                manageUsers, manageItems, approveLoans, viewAllOrders, managePromotions));
        assignPermissionsToRole(saRole, permissionRepository.findAll().stream().collect(Collectors.toSet()));
    }

    private Permission createPermissionIfNotExists(String name) {
        return permissionRepository.findByPermissionName(name)
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setPermissionName(name);
                    return permissionRepository.save(p);
                });
    }

    private Role createRoleIfNotExists(Long id, String name) {
        return roleRepository.findById(id)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleId(id);
                    role.setRoleName(name);
                    return roleRepository.save(role);
                });
    }

    private void assignPermissionsToRole(Role role, Set<Permission> permissions) {
        for (Permission p : permissions) {
            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(p);
            rolePermissionRepository.save(rp);
        }
    }
}
