package com.example.socialnetwork.module.identity.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPrivateResponse extends UserPublicResponse {
    String userId;
    String username;
    String email;
    String phoneNumber;
    Set<String> roles;
    LocalDate createdAt;
    LocalDate updatedAt;
    boolean deleted;
    LocalDate deleteAt;
    boolean banned;
    LocalDate banAt;
    String banReason;
}