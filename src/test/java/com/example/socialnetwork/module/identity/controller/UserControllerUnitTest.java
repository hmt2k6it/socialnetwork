package com.example.socialnetwork.module.identity.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.dto.response.UserResponse;
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

    private UserCreationRequest request;
    private UserResponse userResponse;

    @BeforeEach
    void initData() {
        request = new UserCreationRequest("john", "123456", "john", "john", "john@gmail.com", "123456789", "VN", "MALE",
                new Date());

        userResponse = UserResponse.builder()
                .userId("cf0600f5-388d-4299-bddc-d57367b6670e")
                .username("john")
                .firstName("john")
                .lastName("john")
                .email("john@gmail.com")
                .phoneNumber("123456789")
                .country("VN")
                .gender("MALE")
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        when(userService.createUser(any(UserCreationRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.userId").value("cf0600f5-388d-4299-bddc-d57367b6670e"))
                .andExpect(jsonPath("$.result.username").value("john"))
                .andExpect(jsonPath("$.result.firstName").value("john"))
                .andExpect(jsonPath("$.result.lastName").value("john"))
                .andExpect(jsonPath("$.result.email").value("john@gmail.com"));
    }
}
