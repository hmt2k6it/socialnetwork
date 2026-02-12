package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

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
        roleCreationRequest = RoleCreationRequest.builder().name("USER").build();
    }

    @Test
    void getAllRoles_success() {
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponse);

        List<RoleResponse> response = roleService.getAllRoles();
        assertThat(response.get(0).getName()).isEqualTo(roleResponse.getName());
        assertThat(response.get(0).getPermissions().isEmpty()).isEqualTo(false);
    }

}
