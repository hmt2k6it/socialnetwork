package com.example.socialnetwork.module.identity.dto.request;

import java.time.LocalDate;

import com.example.socialnetwork.module.identity.validate.dob.DobConstraint;

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
public class UserUpdateRequest {
    String firstName;
    String lastName;
    @Email(message = "INVALID_EMAIL")
    String email;
    String phoneNumber;
    String avatar;
    String bio;
    String country;
    String gender;
    @DobConstraint(max = 100, min = 18)
    LocalDate dob;
}
