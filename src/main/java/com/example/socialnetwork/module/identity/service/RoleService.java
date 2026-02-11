package com.example.socialnetwork.module.identity.service;

import java.util.List;

import com.example.socialnetwork.module.identity.dto.request.AssignRoleRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;

public interface RoleService {
    @PreAuthorize("hasRole('ADMIN')")
    List<RoleResponse> getAllRoles();

    @PreAuthorize("hasRole('ADMIN')")
    RoleResponse createRole(RoleCreationRequest role);

    @PreAuthorize("hasRole('ADMIN')")
    String deleteRole(String name);

    @PreAuthorize("hasRole('ADMIN')")
    RoleResponse assignPermissionToRole(String roleId, AssignRoleRequest request);
}
