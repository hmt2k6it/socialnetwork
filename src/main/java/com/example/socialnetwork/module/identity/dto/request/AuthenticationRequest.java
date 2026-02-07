package com.example.socialnetwork.module.identity.dto.request;

import com.example.socialnetwork.module.identity.validate.password.PasswordConstraint;
import com.example.socialnetwork.module.identity.validate.username.UserNameConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @UserNameConstraint(min = 3, max = 16)
    String username;
    @PasswordConstraint(min = 8, max = 32)
    String password;
}
