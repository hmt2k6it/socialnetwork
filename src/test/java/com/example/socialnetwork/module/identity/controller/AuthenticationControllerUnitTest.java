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
        private AuthenticationResponse authResponse;

        @BeforeEach
        void initData() {
                registerRequest = new UserCreationRequest("john", "123456", "john", "john", "john@gmail.com",
                                "123456789", "VN",
                                "MALE", null);

                authRequest = new AuthenticationRequest("john", "123456");

                authResponse = AuthenticationResponse.builder()
                                .accessToken("access_token")
                                .refreshToken("refresh_token")
                                .user(UserResponse.builder()
                                                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                                                .username("john")
                                                .firstName("john")
                                                .lastName("john")
                                                .email("john@gmail.com")
                                                .build())
                                .build();
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
                                .andExpect(jsonPath("$.result.refreshToken").value("refresh_token"));
        }
}
