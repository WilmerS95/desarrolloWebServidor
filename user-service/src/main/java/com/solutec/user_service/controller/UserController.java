package com.solutec.user_service.controller;

import com.solutec.user_service.dto.PermissionDTO;
import com.solutec.user_service.dto.RoleDTO;
import com.solutec.user_service.dto.RoleRequest;
import com.solutec.user_service.dto.UserDTO;
import com.solutec.user_service.entity.Role;
import com.solutec.user_service.entity.User;
import com.solutec.user_service.service.PermissionService;
import com.solutec.user_service.service.RoleService;
import com.solutec.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public UserController(UserService userService, RoleService roleService, PermissionService permissionService) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    private Long extractUserIdFromJwt(Jwt jwt) {
        if (jwt == null) return null;
        Object userIdObj = jwt.getClaims().get("userId");
        if (userIdObj == null) return null;
        if (userIdObj instanceof Number) return ((Number) userIdObj).longValue();
        return Long.valueOf(userIdObj.toString());
    }

    private String extractRoleFromJwt(Jwt jwt) {
        if (jwt == null) return null;
        Object roleObj = jwt.getClaims().get("role");
        return roleObj != null ? roleObj.toString() : null;
    }

    private boolean isAdminRole(String role) {
        return role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("SA") || role.equalsIgnoreCase("SUPER_ADMIN"));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal Jwt jwt) {
        Long requesterId = extractUserIdFromJwt(jwt);
        if (requesterId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(userService.getUserDTOById(requesterId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long requesterId = extractUserIdFromJwt(jwt);
        String role = extractRoleFromJwt(jwt);

        if (requesterId != null && (requesterId.equals(id) || isAdminRole(role))) {
            UserDTO dto = userService.getUserDTOById(id);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("roles/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());

        RoleDTO dto = roleService.createRole(role, request.getPermissionIds());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());

        RoleDTO dto = roleService.updateRole(id, role, request.getPermissionIds());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            @AuthenticationPrincipal Jwt jwt) {

        Long requesterId = extractUserIdFromJwt(jwt);
        String role = extractRoleFromJwt(jwt);
        boolean isAdmin = isAdminRole(role);

        if (requesterId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!isAdmin && !requesterId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado para editar este usuario");
        }

        User saved = userService.updateUserProfile(id, updatedUser, isAdmin);
        UserDTO dto = userService.getUserDTOById(saved.getUserID());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> changeRole(@PathVariable Long id, @RequestParam Long roleId) {
        return ResponseEntity.ok(userService.changeUserRole(id, roleId));
    }
}
