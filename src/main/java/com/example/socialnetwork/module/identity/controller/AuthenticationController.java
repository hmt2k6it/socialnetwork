package com.example.socialnetwork.module.identity.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socialnetwork.common.dto.response.ApiResponse;
import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.ForgetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.LogoutRequest;
import com.example.socialnetwork.module.identity.dto.request.RefreshTokenRequest;
import com.example.socialnetwork.module.identity.dto.request.ResetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.register(request))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }

    @PostMapping("/forget-password")
    public ApiResponse<String> forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        return ApiResponse.<String>builder()
                .result(authenticationService.forgetPassword(request))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ApiResponse.<String>builder()
                .result(authenticationService.resetPassword(request))
                .build();
    }

}
