package com.example.socialnetwork.module.identity.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;
import com.example.socialnetwork.module.identity.entity.Permission;

public interface RoleService {
    @PreAuthorize("hasRole('ADMIN')")
    List<RoleResponse> getAllRoles();

    @PreAuthorize("hasRole('ADMIN')")
    RoleResponse getRoleById(String name);

    @PreAuthorize("hasRole('ADMIN')")
    RoleResponse createRole(RoleCreationRequest role);

    @PreAuthorize("hasRole('ADMIN')")
    String deleteRole(String name);

    @PreAuthorize("hasRole('ADMIN')")
    RoleResponse assignPermissionToRole(String roleId, Set<Permission> permissions);
}
