package com.example.socialnetwork.module.identity.service.impl;

import java.util.HashSet;
import java.util.List;


import com.example.socialnetwork.module.identity.dto.request.AssignRoleRequest;
import com.example.socialnetwork.module.identity.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;
import com.example.socialnetwork.module.identity.entity.Permission;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.mapper.RoleMapper;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleCreationRequest request) {
        if (roleRepository.existsById(request.getName())) {
            throw new  AppException(ErrorCode.ROLE_EXIST);
        }
        Role role = roleMapper.toRole(request);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public String deleteRole(String name) {
        roleRepository.deleteById(name);
        return "Role deleted successfully";
    }

    @Override
    @Transactional
    public RoleResponse assignPermissionToRole(String id, AssignRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        if (permissions.size() != request.getPermissions().size()) {
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

}
