package com.example.socialnetwork.module.identity.controller;

import java.util.List;

import com.example.socialnetwork.module.identity.dto.request.AssignRoleRequest;
import org.springframework.web.bind.annotation.*;

import com.example.socialnetwork.common.dto.response.ApiResponse;
import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;
import com.example.socialnetwork.module.identity.service.RoleService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/roles")
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleCreationRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{roleName}")
    public ApiResponse<String> deleteRole(@PathVariable String roleName) {
        return ApiResponse.<String>builder()
                .result(roleService.deleteRole(roleName))
                .build();
    }

    @PutMapping("/{roleName}")
    public ApiResponse<RoleResponse> assignPermissionToRole(@PathVariable String roleName, @RequestBody AssignRoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.assignPermissionToRole(roleName, request))
                .build();
    }
}
