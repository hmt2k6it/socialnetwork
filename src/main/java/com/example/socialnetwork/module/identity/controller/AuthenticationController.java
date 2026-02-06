package com.example.socialnetwork.module.identity.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.socialnetwork.common.dto.response.ApiResponse;
import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.service.AuthenticationService;

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
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@RequestBody UserCreationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.register(request))
                .build();
    }
}
