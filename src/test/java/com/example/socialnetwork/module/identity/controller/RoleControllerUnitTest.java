package com.example.socialnetwork.module.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import com.example.socialnetwork.module.identity.entity.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.socialnetwork.module.identity.dto.request.RoleCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.RoleResponse;
import com.example.socialnetwork.module.identity.service.RoleService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoleControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoleCreationRequest request;
    private RoleResponse response;

    @BeforeEach
    void initData() {
        Permission permission = Permission.builder()
                .name("USER_READ")
                .build();

        request = RoleCreationRequest.builder()
                .name("USER")
                .permissions(Set.of())
                .build();

        response = RoleResponse.builder()
                .name("USER")
                .permissions(Set.of(permission))
                .build();
    }

    @Test
    void createRole_validRequest_success() throws Exception {
        when(roleService.createRole(any(RoleCreationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.name").value("USER"));
    }

    @Test
    void getAllRoles_success() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result[0].name").value("USER"));
    }

    @Test
    void getRoleById_success() throws Exception {
        when(roleService.getRoleById(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/v1/roles/{roleName}", "USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.name").value("USER"));
    }

    @Test
    void deleteRole_success() throws Exception {
        when(roleService.deleteRole(anyString())).thenReturn("Role deleted successfully");

        mockMvc.perform(delete("/api/v1/roles/{roleName}", "USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").value("Role deleted successfully"));
    }
}
