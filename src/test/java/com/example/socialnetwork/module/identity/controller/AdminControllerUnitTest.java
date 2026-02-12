package com.example.socialnetwork.module.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.socialnetwork.module.identity.dto.request.AssignRoleToUserRequest;
import com.example.socialnetwork.module.identity.dto.request.BanRequest;
import com.example.socialnetwork.module.identity.dto.response.UserPrivateResponse;
import com.example.socialnetwork.module.identity.service.AdminService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrivateResponse userPrivateResponse;
    private BanRequest banRequest;
    private AssignRoleToUserRequest assignRoleToUserRequest;

    private static final String USER_ID = "cf0600f5-388d-4299-bddc-d57367b6670e";

    @BeforeEach
    void initData() {
        userPrivateResponse = UserPrivateResponse.builder()
                .userId(USER_ID)
                .username("nlnq28062007")
                .email("nlnq28062007@gmail.com")
                .roles(Set.of("USER"))
                .build();

        banRequest = BanRequest.builder()
                .userId(USER_ID)
                .reason("Spam")
                .build();

        assignRoleToUserRequest = AssignRoleToUserRequest.builder()
                .userId(USER_ID)
                .roles(Set.of("ADMIN"))
                .build();
    }

    @Test
    void getAllUsers_success() throws Exception {
        when(adminService.getAllUsers()).thenReturn(List.of(userPrivateResponse));

        mockMvc.perform(get("/api/v1/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result[0].userId").value(USER_ID));
    }

    @Test
    void deleteUser_success() throws Exception {
        when(adminService.deleteUser(anyString())).thenReturn(userPrivateResponse);

        mockMvc.perform(delete("/api/v1/admin/users/{userId}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.userId").value(USER_ID));
    }

    @Test
    void banUser_success() throws Exception {
        when(adminService.banUser(any(BanRequest.class))).thenReturn(userPrivateResponse);

        mockMvc.perform(put("/api/v1/admin/users/ban")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(banRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.userId").value(USER_ID));
    }

    @Test
    void assignRoleToUser_success() throws Exception {
        when(adminService.assignRoleToUser(any(AssignRoleToUserRequest.class))).thenReturn(userPrivateResponse);

        mockMvc.perform(put("/api/v1/admin/users/assign-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignRoleToUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.userId").value(USER_ID));
    }
}
