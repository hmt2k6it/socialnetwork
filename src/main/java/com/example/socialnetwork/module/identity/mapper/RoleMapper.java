package com.example.socialnetwork.module.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;
import com.example.socialnetwork.module.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    RoleResponse toRoleResponse(Role role);

}
