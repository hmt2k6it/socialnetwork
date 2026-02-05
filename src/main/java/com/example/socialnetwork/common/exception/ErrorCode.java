package com.example.socialnetwork.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED(000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXIST(201, "Username already exists", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(202, "Role not found", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(203, "User not found", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(204, "Password incorrect", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatus status;
}
