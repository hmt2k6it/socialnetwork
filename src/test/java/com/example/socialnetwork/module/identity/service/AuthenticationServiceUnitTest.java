package com.example.socialnetwork.module.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.ForgetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.LogoutRequest;
import com.example.socialnetwork.module.identity.dto.request.RefreshTokenRequest;
import com.example.socialnetwork.module.identity.dto.request.ResetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.dto.response.UserResponse;
import com.example.socialnetwork.module.identity.entity.RefreshToken;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.RefreshTokenRepository;
import com.example.socialnetwork.module.identity.repository.RoleRepository;
import com.example.socialnetwork.module.identity.repository.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

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
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    CacheManager cacheManager;
    @Mock
    Cache<String, String> otpCache;

    @InjectMocks
    AuthenticationService authenticationService;

    UserCreationRequest registerRequest;
    AuthenticationRequest authRequest;
    AuthenticationResponse authenticationResponse;
    UserResponse userResponse;
    User user;
    Role role;
    RefreshToken refreshToken;
    LogoutRequest logoutRequest;
    ForgetPasswordRequest forgetPasswordRequest;
    ResetPasswordRequest resetPasswordRequest;

    private static final String SIGNER_KEY = "12345678901234567890123456789012";

    @BeforeEach
    void initData() {
        requestDataSetup();

        ReflectionTestUtils.setField(authenticationService, "signerKey", SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "accessTokenExpirationTime", 3600L);
        ReflectionTestUtils.setField(authenticationService, "refreshTokenExpirationTime", 7200L);
    }

    void requestDataSetup() {
        registerRequest = new UserCreationRequest("nlnq28062007", "Nlnq28062007!", "Nguyen Lam Nhu", "Quynh",
                "nlqn28062007@gmail.com", "03475647566", "Binh Duong", "FEMALE", null);
        authRequest = new AuthenticationRequest("nlnq28062007", "Nlnq28062007@gmail.com");

        role = Role.builder().name("USER").build();

        user = User.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("nlnq28062007")
                .password("encoded_password")
                .email("Nlnq28062007@gmail.com")
                .phoneNumber("03475647566")
                .country("Binh Duong")
                .gender("FEMALE")
                .roles(Set.of(role))
                .build();

        userResponse = UserResponse.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("nlnq28062007")
                .build();

        authenticationResponse = AuthenticationResponse.builder()
                .user(userResponse)
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .build();

        refreshToken = RefreshToken.builder()
                .jti("refresh_token_jti")
                .user(user)
                .expiryDate(new Date(System.currentTimeMillis() + 10000))
                .build();

        forgetPasswordRequest = new ForgetPasswordRequest("nlnq28062007@gmail.com");

        resetPasswordRequest = new ResetPasswordRequest("nlnq28062007@gmail.com", "123456", "Nlnq28062007!",
                "Nlnq28062007!");

        String validToken = generateValidToken();
        logoutRequest = LogoutRequest.builder()
                .accessToken(validToken)
                .refreshToken(validToken)
                .build();
    }

    private String generateValidToken() {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .jwtID("refresh_token_jti")
                    .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                    .build();
            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (Exception e) {
            return null;
        }
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
        assertThat(response.getUser().getUsername()).isEqualTo("nlnq28062007");
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

    @Test
    void logout_validRequest_success() {
        org.springframework.cache.Cache springCache = mock(org.springframework.cache.Cache.class);
        when(cacheManager.getCache("invalidated_tokens")).thenReturn(springCache);

        authenticationService.logout(logoutRequest);

        verify(refreshTokenRepository).deleteById(anyString());
    }

    @Test
    void refreshToken_validRequest_success() {
        String validToken = generateValidToken();
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(validToken)
                .build();

        when(refreshTokenRepository.findById(anyString())).thenReturn(Optional.of(refreshToken));

        var response = authenticationService.refreshToken(request);

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        verify(refreshTokenRepository).deleteById(anyString());
    }

    @Test
    void refreshToken_tokenNotFound_fail() {
        String validToken = generateValidToken();
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(validToken)
                .build();

        when(refreshTokenRepository.findById(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> authenticationService.refreshToken(request));

        assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.UNAUTHENTICATED.getCode());
    }

    @Test
    void forgetPassword_validRequest_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        String result = authenticationService.forgetPassword(forgetPasswordRequest);

        assertThat(result).isEqualTo("If user exists, OTP has been sent to your email");
        verify(otpCache).put(anyString(), anyString());
    }

    @Test
    void resetPassword_validRequest_success() {
        when(otpCache.getIfPresent("nlnq28062007@gmail.com")).thenReturn("123456");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        String response = authenticationService.resetPassword(resetPasswordRequest);

        assertThat(response).isEqualTo("Password reset successfully");

        verify(otpCache).invalidate("nlnq28062007@gmail.com");
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_invalidOtp_fail() {
        when(otpCache.getIfPresent("nlnq28062007@gmail.com")).thenReturn(null);

        var exception = assertThrows(AppException.class,
                () -> authenticationService.resetPassword(resetPasswordRequest));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_OTP);
    }

}