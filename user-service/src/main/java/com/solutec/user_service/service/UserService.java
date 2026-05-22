package com.solutec.user_service.service;

import com.solutec.user_service.dto.UserDTO;
import com.solutec.user_service.entity.*;
import com.solutec.user_service.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public UserDTO getUserDTOById(Long id) {
        return userRepository.findById(id)
            .map(this::mapToDTO)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
            .map(user -> {
                user.setUsername(updatedUser.getUsername());
                user.setEmail(updatedUser.getEmail());
                user.setFirstName(updatedUser.getFirstName());
                user.setSecondOrMoreNames(updatedUser.getSecondOrMoreNames());
                user.setFirstLastName(updatedUser.getFirstLastName());
                user.setSecondLastName(updatedUser.getSecondLastName());
                user.setMarriedLastName(updatedUser.getMarriedLastName());
                user.setTelephone(updatedUser.getTelephone());
                user.setAddress(updatedUser.getAddress());
                return userRepository.save(user);
            }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User changeUserRole(Long id, Long roleId) {
        return userRepository.findById(id)
                .map(user -> {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    user.setRole(role);
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


    public User updateUserProfile(Long id, User updatedUser, boolean isAdmin) {
        return userRepository.findById(id)
            .map(user -> {
                user.setUsername(updatedUser.getUsername());
                user.setEmail(updatedUser.getEmail());
                user.setFirstName(updatedUser.getFirstName());
                user.setSecondOrMoreNames(updatedUser.getSecondOrMoreNames());
                user.setFirstLastName(updatedUser.getFirstLastName());
                user.setSecondLastName(updatedUser.getSecondLastName());
                user.setMarriedLastName(updatedUser.getMarriedLastName());
                user.setTelephone(updatedUser.getTelephone());
                user.setAddress(updatedUser.getAddress());

                if (isAdmin && updatedUser.getRole() != null && updatedUser.getRole().getRoleId() != null) {
                    Long newRoleId = updatedUser.getRole().getRoleId();
                    Role role = roleRepository.findById(newRoleId)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    user.setRole(role);
                }

                return userRepository.save(user);
            }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserID(user.getUserID());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setSecondOrMoreNames(user.getSecondOrMoreNames());
        dto.setFirstLastName(user.getFirstLastName());
        dto.setSecondLastName(user.getSecondLastName());
        dto.setMarriedLastName(user.getMarriedLastName());
        dto.setTelephone(user.getTelephone());
        dto.setAddress(user.getAddress());
        dto.setDpiOrPassport(user.getDpiOrPassport());
        dto.setPhotoUrl(user.getPhotoUrl());

        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRoleName());

            Set<String> permissions = user.getRole().getRolePermissions().stream()
                    .map(rp -> rp.getPermission().getPermissionName())
                    .collect(Collectors.toSet());
            dto.setPermissions(permissions);
        }
        return dto;
    }
}