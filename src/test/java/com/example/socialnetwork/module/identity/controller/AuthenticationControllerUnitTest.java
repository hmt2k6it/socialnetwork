package com.example.socialnetwork.module.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.ForgetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.LogoutRequest;
import com.example.socialnetwork.module.identity.dto.request.RefreshTokenRequest;
import com.example.socialnetwork.module.identity.dto.request.ResetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.dto.response.UserResponse;
import com.example.socialnetwork.module.identity.service.AuthenticationService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerUnitTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthenticationService authenticationService;

        @Autowired
        private ObjectMapper objectMapper;

        private UserCreationRequest registerRequest;
        private AuthenticationRequest authRequest;
        private RefreshTokenRequest refreshTokenRequest;
        private LogoutRequest logoutRequest;
        private AuthenticationResponse authResponse;
        private ForgetPasswordRequest forgetPasswordRequest;
        private ResetPasswordRequest resetPasswordRequest;

        @BeforeEach
        void initData() {
                registerRequest = new UserCreationRequest("nlnq28062007", "Nlnq28062007!", "Nguyen Lam Nhu", "Quynh",
                                "nlnq28062007@gmail.com",
                                "0369874561", "VN",
                                "FEMALE", null);

                authRequest = new AuthenticationRequest("nlnq28062007", "Nlnq28062007!");

                refreshTokenRequest = RefreshTokenRequest.builder()
                                .refreshToken("refresh_token")
                                .build();

                logoutRequest = LogoutRequest.builder()
                                .refreshToken("refresh_token")
                                .build();

                authResponse = AuthenticationResponse.builder()
                                .accessToken("access_token")
                                .refreshToken("refresh_token")
                                .user(UserResponse.builder()
                                                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                                                .username("nlnq28062007")
                                                .firstName("Nguyen Lam Nhu")
                                                .lastName("Quynh")
                                                .email("nlnq28062007@gmail.com")
                                                .build())
                                .build();
                forgetPasswordRequest = new ForgetPasswordRequest("nlnq28062007@gmail.com");
                resetPasswordRequest = new ResetPasswordRequest("nlnq28062007@gmail.com", "otp", "Nlnq28062007!",
                                "Nlnq28062007!");

        }

        @Test
        void register_validRequest_success() throws Exception {
                when(authenticationService.register(any(UserCreationRequest.class))).thenReturn(authResponse);

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.result.user.userId")
                                                .value("cf0600f5-388d-4299-bddc-d57367b6670e"));
        }

        @Test
        void authenticate_validRequest_success() throws Exception {
                when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(authResponse);

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.result.accessToken").value("access_token"))
                                .andExpect(jsonPath("$.result.refreshToken").value("refresh_token"))
                                .andExpect(jsonPath("$.result.user.userId")
                                                .value("cf0600f5-388d-4299-bddc-d57367b6670e"));
        }

        @Test
        void refreshToken_validRequest_success() throws Exception {
                AuthenticationResponse refreshResponse = AuthenticationResponse.builder()
                                .accessToken("access_token")
                                .refreshToken("refresh_token")
                                .user(null)
                                .build();
                when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(refreshResponse);

                mockMvc.perform(post("/api/v1/auth/refresh-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.result.accessToken").value("access_token"))
                                .andExpect(jsonPath("$.result.refreshToken").value("refresh_token"))
                                .andExpect(jsonPath("$.result.user").doesNotExist());
        }

        @Test
        void logout_validRequest_success() throws Exception {
                authenticationService.logout(logoutRequest);

                mockMvc.perform(post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(logoutRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void forgetPassword_validRequest_success() throws Exception {
                when(authenticationService.forgetPassword(any(ForgetPasswordRequest.class)))
                                .thenReturn("If user exists, OTP has been sent to your email");

                mockMvc.perform(post("/api/v1/auth/forget-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(forgetPasswordRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.result")
                                                .value("If user exists, OTP has been sent to your email"));
        }

        @Test
        void resetPassword_validRequest_success() throws Exception {
                when(authenticationService.resetPassword(any(ResetPasswordRequest.class)))
                                .thenReturn("Password has been reset successfully");

                mockMvc.perform(post("/api/v1/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.result").value("Password has been reset successfully"));
        }

}
