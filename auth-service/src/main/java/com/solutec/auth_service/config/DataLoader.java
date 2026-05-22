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
        // Nuevos Permisos Deportivos
        Permission configTournament = createPermissionIfNotExists("CONFIG_TOURNAMENT"); // Crear ligas, calendarios
        Permission manageTeams = createPermissionIfNotExists("MANAGE_TEAMS");         // Inscribir equipos y jugadores
        Permission recordMatchEvents = createPermissionIfNotExists("RECORD_MATCH_EVENTS"); // Goles, tarjetas (Árbitros)
        Permission manageFinances = createPermissionIfNotExists("MANAGE_FINANCES");     // Pagos, deudas, multas
        Permission viewStats = createPermissionIfNotExists("VIEW_STATS");               // Tablas de posiciones, goleadores

        // Nuevos Roles
        Role adminLigaRole = createRoleIfNotExists("ADMIN_LIGA");
        Role delegadoRole = createRoleIfNotExists("DELEGADO");
        Role arbitroRole = createRoleIfNotExists("ARBITRO");
        Role saRole = createRoleIfNotExists("SA"); // Super Admin se mantiene

        // Asignación de permisos estratégicos
        assignPermissionsToRole(delegadoRole, Set.of(viewStats, manageTeams));
        assignPermissionsToRole(arbitroRole, Set.of(viewStats, recordMatchEvents));
        assignPermissionsToRole(adminLigaRole, Set.of(configTournament, manageTeams, recordMatchEvents, manageFinances, viewStats));
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