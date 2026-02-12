package com.example.socialnetwork.module.identity.controller;

import com.example.socialnetwork.common.dto.response.ApiResponse;
import com.example.socialnetwork.module.identity.dto.request.AssignRoleToUserRequest;
import com.example.socialnetwork.module.identity.dto.request.BanRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.service.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService adminService;
    @GetMapping("/users")
    public ApiResponse<List<UserPrivateResponse>> getAllUsers() {
        return ApiResponse.<List<UserPrivateResponse>>builder()
                .result(adminService.getAllUsers())
                .build();
    }
    @DeleteMapping("/users/{userId}")
    public ApiResponse<UserPrivateResponse> deleteUser(@PathVariable String userId) {
        return ApiResponse.<UserPrivateResponse>builder()
                .result(adminService.deleteUser(userId))
                .build();
    }
    @PutMapping ("/users/ban")
    public ApiResponse<UserPrivateResponse> banUser(@RequestBody BanRequest request) {
        return ApiResponse.<UserPrivateResponse>builder()
                .result(adminService.banUser(request))
                .build();
    }
        @PutMapping("/users/assign-role")
    public ApiResponse<UserPrivateResponse> assignRoleToUser(@RequestBody AssignRoleToUserRequest request) {
        return ApiResponse.<UserPrivateResponse>builder()
                .result(adminService.assignRoleToUser(request))
                .build();
    }
}
