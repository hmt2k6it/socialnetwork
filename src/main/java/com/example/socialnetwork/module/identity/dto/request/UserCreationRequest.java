package com.example.socialnetwork.module.identity.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreationRequest {
    String username;
    String password;
    String email;
    String phoneNumber;
    String country;
    String gender;
    Date dob;
}
