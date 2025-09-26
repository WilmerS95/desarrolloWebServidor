package com.solutec.user_service.service;

import com.solutec.user_service.dto.PermissionDTO;
import com.solutec.user_service.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(p -> new PermissionDTO(p.getPermissionId(), p.getPermissionName()))
                .toList();
    }
}
