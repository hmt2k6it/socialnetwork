package com.example.socialnetwork.module.identity.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {
    String username;
    String password;
    String email;
    String phoneNumber;
    String country;
    String gender;
    Date dob;
}
