package com.example.socialnetwork.module.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.socialnetwork.module.identity.dto.request.PermissionCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.PermissionResponse;
import com.example.socialnetwork.module.identity.service.PermissionService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PermissionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    private PermissionCreationRequest request;
    private PermissionResponse response;

    @BeforeEach
    void initData() {
        request = PermissionCreationRequest.builder()
                .name("USER_READ")
                .build();

        response = PermissionResponse.builder()
                .name("USER_READ")
                .build();
    }

    @Test
    void createPermission_validRequest_success() throws Exception {
        when(permissionService.createPermission(any(PermissionCreationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.name").value("USER_READ"));
    }

    @Test
    void getAllPermissions_success() throws Exception {
        when(permissionService.getAllPermissions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/permissions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result[0].name").value("USER_READ"));
    }

    @Test
    void deletePermission_success() throws Exception {
        when(permissionService.deletePermission(any())).thenReturn("Permission deleted successfully");

        mockMvc.perform(delete("/api/v1/permissions/{permissionName}", "USER_READ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").value("Permission deleted successfully"));
    }
}
