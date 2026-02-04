package com.example.socialnetwork.module.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.UserResponse;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User save);

    default String map(Role role) {
        return role.getName();
    }

}
