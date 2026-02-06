package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.dto.response.UserResponse;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthenticationService authenticationService;

    UserCreationRequest registerRequest;
    AuthenticationRequest authRequest;
    AuthenticationResponse authenticationResponse;
    UserResponse userResponse;
    User user;
    Role role;

    @BeforeEach
    void initData() {
        requestDataSetup();

        ReflectionTestUtils.setField(authenticationService, "secretKey", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(authenticationService, "accessTokenExpirationTime", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshTokenExpirationTime", 7200L);
    }

    void requestDataSetup() {
        registerRequest = new UserCreationRequest("john", "123456", "john", "john", "john@gmail.com", "123456789", "VN",
                "MALE", null);
        authRequest = new AuthenticationRequest("john", "123456");

        role = Role.builder().name("USER").build();

        user = User.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("john")
                .password("encoded_password")
                .roles(Set.of(role))
                .build();
        userResponse = UserResponse.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("john")
                .build();
        authenticationResponse = AuthenticationResponse.builder()
                .user(userResponse)
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .build();
    }

    @Test
    void register_validRequest_success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(userMapper.toUser(any(UserCreationRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        var response = authenticationService.register(registerRequest);

        assertThat(response.getUser().getUserId()).isEqualTo("cf0600f5-388d-4299-bddc-d57367b6670e");
        assertThat(response.getUser().getUsername()).isEqualTo("john");
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }

    @Test
    void register_usernameExists_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        var exception = assertThrows(AppException.class, () -> authenticationService.register(registerRequest));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USERNAME_EXIST.getCode());
    }

    @Test
    void register_roleNotFound_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> authenticationService.register(registerRequest));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND.getCode());
    }

    @Test
    void authenticate_validRequest_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);
        var response = authenticationService.authenticate(authRequest);

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getUser()).isNotNull();
    }

    @Test
    void authenticate_userNotFound_fail() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> authenticationService.authenticate(authRequest));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
    }

    @Test
    void authenticate_passwordIncorrect_fail() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        var exception = assertThrows(AppException.class, () -> authenticationService.authenticate(authRequest));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.PASSWORD_INCORRECT.getCode());
    }
}
