package com.example.socialnetwork.module.identity.dto.request;

import com.example.socialnetwork.module.identity.validate.password.PasswordConstraint;

import jakarta.validation.constraints.Email;
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
public class ResetPasswordRequest {
    @Email(message = "INVALID_EMAIL")
    String email;
    String otp;
    @PasswordConstraint(min = 8, max = 32)
    String password;
    @PasswordConstraint(min = 8, max = 32)
    String confirmPassword;
}
