package com.example.socialnetwork.module.identity.dto.response;

import java.util.Date;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userId;
    String username;
    String email;
    String phoneNumber;
    String avatar;
    String bio;
    String country;
    String gender;
    Date dob;
    Set<String> roles;
    Date createdAt;
    Date updatedAt;
}