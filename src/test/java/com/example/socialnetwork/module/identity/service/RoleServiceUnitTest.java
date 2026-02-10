package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;
import com.example.socialnetwork.module.identity.entity.Permission;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.mapper.RoleMapper;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.service.impl.RoleServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RoleServiceUnitTest {
    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleMapper roleMapper;

    @InjectMocks
    RoleServiceImpl roleService;

    RoleCreationRequest roleCreationRequest;
    RoleResponse roleResponse;
    Role role;
    Permission permission;

    @BeforeEach
    void initData() {
        permission = Permission.builder().name("USER_READ").build();
        role = Role.builder().name("USER").permissions(Set.of(permission)).build();
        roleResponse = RoleResponse.builder().name("USER").permissions(Set.of(permission)).build();
        roleCreationRequest = RoleCreationRequest.builder().name("USER").permissions(Set.of(permission)).build();
    }

    @Test
    void getAllRoles_success() {
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponse);

        List<RoleResponse> response = roleService.getAllRoles();
        assertThat(response.getFirst().getName()).isEqualTo(roleResponse.getName());
        assertThat(response.getFirst().getPermissions().isEmpty()).isEqualTo(false);
    }

    @Test
    void getRoleById_success() {
        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponse);

        RoleResponse response = roleService.getRoleById(role.getName());

        assertThat(response.getName()).isEqualTo(role.getName());
        assertThat(response.getPermissions().isEmpty()).isEqualTo(false);
    }

    @Test
    void getRoleById_roleNotFound_fail() {
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, ()-> roleService.getRoleById(role.getName()));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND);
    }

    @Test
    void createRole_success() {
        when(roleRepository.existsById(anyString())).thenReturn(false);
        when(roleMapper.toRole(any(RoleCreationRequest.class))).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponse);

        RoleResponse response = roleService.createRole(roleCreationRequest);

        assertThat(response.getName()).isEqualTo(roleCreationRequest.getName());
        assertThat(response.getPermissions().isEmpty()).isEqualTo(false);
    }

    @Test
    void createRole_roleExists_fail() {
        when(roleRepository.existsById(anyString())).thenReturn(true);

        AppException exception = assertThrows(AppException.class, ()-> roleService.createRole(roleCreationRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROLE_EXIST);
    }

    @Test
    void deleteRole_success() {
        String response = roleService.deleteRole(role.getName());
        assertThat(response).isEqualTo("Role deleted successfully");
        verify(roleRepository).deleteById(anyString());
    }

    @Test
    void assignPermissionToRole_success() {
        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponse response = roleService.assignPermissionToRole(role.getName(), role.getPermissions());

        assertThat(response.getName()).isEqualTo(role.getName());
        assertThat(response.getPermissions().isEmpty()).isEqualTo(false);
    }
    @Test
    void assignPermissionToRole_roleNotFound_fail() {
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, ()-> roleService.assignPermissionToRole(role.getName(), role.getPermissions()));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND);
    }
}
