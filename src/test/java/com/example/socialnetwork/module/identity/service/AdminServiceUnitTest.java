package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.AssignRoleToUserRequest;
import com.example.socialnetwork.module.identity.dto.request.BanRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.repository.UserRepository;
import com.example.socialnetwork.module.identity.service.impl.AdminServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    AdminServiceImpl adminService;

    User user;
    UserPrivateResponse userPrivateResponse;
    Role role;
    BanRequest banRequest;
    AssignRoleToUserRequest assignRoleToUserRequest;

    private static final String USER_ID = "cf0600f5-388d-4299-bddc-d57367b6670e";
    private static final String ROLE_NAME = "USER";

    @BeforeEach
    void setUp() {
        role = Role.builder().name(ROLE_NAME).build();

        user = User.builder()
                .userId(USER_ID)
                .username("testuser")
                .roles(new HashSet<>(Set.of(role)))
                .build();

        userPrivateResponse = UserPrivateResponse.builder()
                .userId(USER_ID)
                .username("testuser")
                .roles(Set.of(ROLE_NAME))
                .build();

        banRequest = BanRequest.builder()
                .userId(USER_ID)
                .reason("Spam")
                .build();

        assignRoleToUserRequest = AssignRoleToUserRequest.builder()
                .userId(USER_ID)
                .roles(Set.of(ROLE_NAME))
                .build();
    }

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserPrivateResponse(any(User.class))).thenReturn(userPrivateResponse);

        List<UserPrivateResponse> result = adminService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(USER_ID);
        verify(userRepository).findAll();
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserPrivateResponse(any(User.class))).thenReturn(userPrivateResponse);

        UserPrivateResponse result = adminService.deleteUser(USER_ID);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(userRepository).save(user);
        verify(userMapper).toUserPrivateResponse(user);
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> adminService.deleteUser(USER_ID));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void banUser_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserPrivateResponse(any(User.class))).thenReturn(userPrivateResponse);

        UserPrivateResponse result = adminService.banUser(banRequest);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(userRepository).save(user);
        verify(userMapper).toUserPrivateResponse(user);
    }

    @Test
    void banUser_userNotFound_throwsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> adminService.banUser(banRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void assignRoleToUser_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(any())).thenReturn(List.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserPrivateResponse(any(User.class))).thenReturn(userPrivateResponse);

        UserPrivateResponse result = adminService.assignRoleToUser(assignRoleToUserRequest);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(userRepository).save(user);
        verify(userMapper).toUserPrivateResponse(user);
    }

    @Test
    void assignRoleToUser_userNotFound_throwsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> adminService.assignRoleToUser(assignRoleToUserRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
