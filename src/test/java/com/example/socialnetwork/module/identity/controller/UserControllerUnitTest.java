package com.example.socialnetwork.module.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.socialnetwork.module.identity.dto.request.UserUpdateRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.dto.response.UserPublicResponse;
import com.example.socialnetwork.module.identity.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrivateResponse userPrivateResponse;
    private UserPublicResponse userPublicResponse;
    private UserUpdateRequest userUpdateRequest;

    private static final String USER_ID = "cf0600f5-388d-4299-bddc-d57367b6670e";
    private static final String USERNAME = "nlnq28062007";

    @BeforeEach
    void initData() {
        userPrivateResponse = UserPrivateResponse.builder()
                .userId(USER_ID)
                .username(USERNAME)
                .firstName("Nguyen Lam Nhu")
                .lastName("Quynh")
                .email("nlnq28062007@gmail.com")
                .phoneNumber("0123456789")
                .avatar("avatar.jpg")
                .bio("Hello World")
                .country("Vietnam")
                .gender("MALE")
                .dob(LocalDate.of(2000, 1, 1))
                .roles(Set.of("USER"))
                .build();

        userPublicResponse = UserPublicResponse.builder()
                .firstName("Nguyen Lam Nhu")
                .lastName("Quynh")
                .avatar("avatar.jpg")
                .bio("Hello World")
                .country("Vietnam")
                .gender("FEMALE")
                .dob(LocalDate.of(2000, 1, 1))
                .build();

        userUpdateRequest = UserUpdateRequest.builder()
                .firstName("Nguyen Lam Nhu Updated")
                .lastName("Quynh Updated")
                .bio("Updated bio")
                .build();
    }

    @Test
    void getUserProfile_validUserId_success() throws Exception {
        when(userService.getUserProfile(anyString())).thenReturn(userPublicResponse);

        mockMvc.perform(get("/api/v1/users/{userId}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.firstName").value("Nguyen Lam Nhu"))
                .andExpect(jsonPath("$.result.lastName").value("Quynh"));
    }

    @Test
    void getMyProfile_validRequest_success() throws Exception {
        when(userService.getMyProfile()).thenReturn(userPrivateResponse);

        mockMvc.perform(get("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.userId").value(USER_ID))
                .andExpect(jsonPath("$.result.username").value(USERNAME))
                .andExpect(jsonPath("$.result.email").value("nlnq28062007@gmail.com"));
    }

    @Test
    void updateMyProfile_validRequest_success() throws Exception {
        when(userService.updateMyProfile(any(UserUpdateRequest.class))).thenReturn(userPrivateResponse);

        mockMvc.perform(patch("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.userId").value(USER_ID));
    }
}
