package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.socialnetwork.module.identity.dto.request.PermissionCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.PermissionResponse;
import com.example.socialnetwork.module.identity.entity.Permission;
import com.example.socialnetwork.module.identity.mapper.PermissionMapper;
import com.example.socialnetwork.module.identity.repository.PermissionRepository;
import com.example.socialnetwork.module.identity.service.impl.PermissionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceUnitTest {

    @Mock
    PermissionRepository permissionRepository;

    @Mock
    PermissionMapper permissionMapper;

    @InjectMocks
    PermissionServiceImpl permissionService;

    PermissionCreationRequest request;
    PermissionResponse response;
    Permission permission;

    @BeforeEach
    void initData() {
        request = PermissionCreationRequest.builder().name("APPROVE_POST").build();
        response = PermissionResponse.builder().name("APPROVE_POST").build();
        permission = Permission.builder().name("APPROVE_POST").build();
    }

    @Test
    void createPermission_success() {
        // GIVEN
        when(permissionMapper.toPermission(any())).thenReturn(permission);
        when(permissionRepository.save(any())).thenReturn(permission);
        when(permissionMapper.toPermissionResponse(any())).thenReturn(response);

        // WHEN
        var result = permissionService.createPermission(request);

        // THEN
        assertThat(result.getName()).isEqualTo("APPROVE_POST");
        verify(permissionRepository).save(any());
    }

    @Test
    void getAllPermissions_success() {
        // GIVEN
        when(permissionRepository.findAll()).thenReturn(List.of(permission));
        when(permissionMapper.toPermissionResponse(any())).thenReturn(response);

        // WHEN
        var result = permissionService.getAllPermissions();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("APPROVE_POST");
    }

    @Test
    void deletePermission_success() {
        // WHEN
        String result = permissionService.deletePermission("APPROVE_POST");

        // THEN
        assertThat(result).isEqualTo("Permission deleted successfully");
        verify(permissionRepository).deleteById(anyString());
    }
}