package com.example.socialnetwork.module.identity.mapper;

import org.mapstruct.Mapper;

import com.example.socialnetwork.module.identity.dto.request.PermissionCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.PermissionResponse;
import com.example.socialnetwork.module.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreationRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
