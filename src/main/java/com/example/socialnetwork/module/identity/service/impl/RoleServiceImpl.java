package com.example.socialnetwork.module.identity.service.impl;

import java.util.List;
import java.util.Set;

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

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @Override
    public RoleResponse getRoleById(String name) {
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return roleMapper.toRoleResponse(role);
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
    public RoleResponse assignPermissionToRole(String id, Set<Permission> permissions) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        role.setPermissions(permissions);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

}
