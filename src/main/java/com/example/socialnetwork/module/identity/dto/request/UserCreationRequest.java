package com.example.socialnetwork.module.identity.dto.request;

import java.time.LocalDate;

import com.example.socialnetwork.module.identity.validate.dob.DobConstraint;
import com.example.socialnetwork.module.identity.validate.password.PasswordConstraint;
import com.example.socialnetwork.module.identity.validate.username.UserNameConstraint;

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
public class UserCreationRequest {
    @UserNameConstraint(min = 3, max = 16)
    String username;
    @PasswordConstraint(min = 8, max = 32)
    String password;
    String firstName;
    String lastName;
    @Email(message = "INVALID_EMAIL")
    String email;
    String phoneNumber;
    String country;
    String gender;
    @DobConstraint(min = 18, max = 100)
    LocalDate dob;
}
