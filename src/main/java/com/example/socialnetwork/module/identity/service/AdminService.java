package com.example.socialnetwork.module.identity.service;

import com.example.socialnetwork.module.identity.dto.request.AssignRoleToUserRequest;
import com.example.socialnetwork.module.identity.dto.request.BanRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AdminService {
    @PreAuthorize("hasRole('ADMIN')")
    List<UserPrivateResponse> getAllUsers();

    @PreAuthorize("hasRole('ADMIN')")
    UserPrivateResponse deleteUser(String userId);

    @PreAuthorize("hasRole('ADMIN')")
    UserPrivateResponse banUser(BanRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    UserPrivateResponse assignRoleToUser(AssignRoleToUserRequest request);

}
