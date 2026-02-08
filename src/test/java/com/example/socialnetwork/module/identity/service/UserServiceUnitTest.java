package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.UserUpdateRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.dto.response.UserPublicResponse;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.UserRepository;
import com.example.socialnetwork.module.identity.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @InjectMocks
    UserServiceImpl userService;

    User user;
    UserPrivateResponse userPrivateResponse;
    UserPublicResponse userPublicResponse;
    UserUpdateRequest userUpdateRequest;
    Role role;

    private static final String USER_ID = "cf0600f5-388d-4299-bddc-d57367b6670e";
    private static final String USERNAME = "nlqn28062007";

    @BeforeEach
    void setUp() {
        role = Role.builder().name("USER").build();

        user = User.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .firstName("Nguyen Lam Nhu")
                .lastName("Quynh")
                .email("nlnq28062007@gmail.com")
                .phoneNumber("0123456789")
                .avatar("avatar.jpg")
                .bio("Hello World")
                .country("Vietnam")
                .gender("FEMALE")
                .dob(LocalDate.of(2000, 1, 1))
                .roles(Set.of(role))
                .build();

        userPrivateResponse = UserPrivateResponse.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .firstName("Nguyen Lam Nhu")
                .lastName("Quynh")
                .email("nlnq28062007@gmail.com")
                .phoneNumber("0123456789")
                .roles(Set.of("USER"))
                .build();

        userPublicResponse = UserPublicResponse.builder()
                .firstName("Nguyen Lam Nhu")
                .lastName("Quynh")
                .avatar("avatar.jpg")
                .bio("Hello World")
                .country("Vietnam")
                .gender("FEMALE")
                .build();

        userUpdateRequest = UserUpdateRequest.builder()
                .firstName("Nguyen Lam Nhu Updated")
                .lastName("Quynh Updated")
                .bio("Updated bio")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(USER_ID);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getMyProfile_authenticatedUser_success() {
        mockAuthenticatedUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toUserPrivateResponse(user)).thenReturn(userPrivateResponse);

        UserPrivateResponse result = userService.getMyProfile();

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo("nlnq28062007@gmail.com");
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toUserPrivateResponse(user);
    }

    @Test
    void getMyProfile_nullAuthentication_throwsUnauthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        AppException exception = assertThrows(AppException.class, () -> userService.getMyProfile());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void getMyProfile_notAuthenticated_throwsUnauthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        AppException exception = assertThrows(AppException.class, () -> userService.getMyProfile());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void getMyProfile_anonymousUser_throwsUnauthenticated() {
        AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                "key", "anonymousUser",
                java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        when(securityContext.getAuthentication()).thenReturn(anonymousToken);
        SecurityContextHolder.setContext(securityContext);

        AppException exception = assertThrows(AppException.class, () -> userService.getMyProfile());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void getMyProfile_userNotFound_throwsUserNotFound() {
        mockAuthenticatedUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.getMyProfile());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void updateMyProfile_validRequest_success() {
        mockAuthenticatedUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserPrivateResponse(user)).thenReturn(userPrivateResponse);

        UserPrivateResponse result = userService.updateMyProfile(userUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(userMapper).updateUserFromRequest(userUpdateRequest, user);
        verify(userRepository).save(user);
        verify(userMapper).toUserPrivateResponse(user);
    }

    @Test
    void updateMyProfile_notAuthenticated_throwsUnauthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        AppException exception = assertThrows(AppException.class,
                () -> userService.updateMyProfile(userUpdateRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void updateMyProfile_userNotFound_throwsUserNotFound() {
        mockAuthenticatedUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> userService.updateMyProfile(userUpdateRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void getUserProfile_validUserId_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toUserPublicResponse(user)).thenReturn(userPublicResponse);

        UserPublicResponse result = userService.getUserProfile(USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Nguyen Lam Nhu");
        assertThat(result.getLastName()).isEqualTo("Quynh");
        assertThat(result.getAvatar()).isEqualTo("avatar.jpg");
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toUserPublicResponse(user);
    }

    @Test
    void getUserProfile_userNotFound_throwsUserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> userService.getUserProfile("non-existent-id"));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
