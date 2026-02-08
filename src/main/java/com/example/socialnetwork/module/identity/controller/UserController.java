package com.example.socialnetwork.module.identity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socialnetwork.common.dto.response.ApiResponse;
import com.example.socialnetwork.module.identity.dto.request.UserUpdateRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.dto.response.UserPublicResponse;
import com.example.socialnetwork.module.identity.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
public class UserController {
    UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserPublicResponse> getUserProfile(@PathVariable String userId) {
        return ApiResponse.<UserPublicResponse>builder()
                .result(userService.getUserProfile(userId))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<UserPrivateResponse> getMyProfile() {
        return ApiResponse.<UserPrivateResponse>builder()
                .result(userService.getMyProfile())
                .build();
    }

    @PatchMapping("/me")
    public ApiResponse<UserPrivateResponse> updateMyProfile(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserPrivateResponse>builder()
                .result(userService.updateMyProfile(userUpdateRequest))
                .build();
    }

}
