package com.example.socialnetwork.module.identity.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.socialnetwork.module.identity.dto.request.PermissionCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.PermissionResponse;

public interface PermissionService {
    @PreAuthorize("hasRole('ADMIN')")
    PermissionResponse createPermission(PermissionCreationRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    String deletePermission(String name);

    @PreAuthorize("hasRole('ADMIN')")
    List<PermissionResponse> getAllPermissions();
}
