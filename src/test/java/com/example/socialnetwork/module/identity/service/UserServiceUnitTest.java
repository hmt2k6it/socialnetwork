package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.UserResponse;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    UserCreationRequest request;
    UserResponse userResponse;
    User user;
    Role role;

    @BeforeEach
    void initData() {
        request = new UserCreationRequest("john", "123456", "john", "john", "john@gmail.com", "123456789", "VN", "MALE",
                null);
        userResponse = UserResponse.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("john")
                .firstName("john")
                .lastName("john")
                .email("john@gmail.com")
                .build();
        user = User.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("john")
                .email("john@gmail.com")
                .build();
        role = Role.builder()
                .name("USER")
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        when(userMapper.toUser(any(UserCreationRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        var response = userService.createUser(request);

        assertThat(response.getUserId()).isEqualTo("cf0600f5-388d-4299-bddc-d57367b6670e");
        assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    void createUser_userExisted_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.USERNAME_EXIST.getCode());
    }

    @Test
    void createUser_roleNotFound_fail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> userService.createUser(request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND.getCode());
    }
}
