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
        UNCATEGORIZED(1000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
        INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
        USERNAME_EXIST(201, "Username already exists", HttpStatus.BAD_REQUEST),
        ROLE_NOT_FOUND(202, "Role not found", HttpStatus.BAD_REQUEST),
        USER_NOT_FOUND(203, "User not found", HttpStatus.BAD_REQUEST),
        PASSWORD_INCORRECT(204, "Password incorrect", HttpStatus.BAD_REQUEST),
        UNAUTHENTICATED(205, "Unauthenticated", HttpStatus.UNAUTHORIZED),
        OTP_EXPIRED(206, "OTP expired", HttpStatus.BAD_REQUEST),
        INVALID_OTP(207, "Invalid OTP", HttpStatus.BAD_REQUEST),
        PASSWORD_NOT_MATCH(208, "Password not match", HttpStatus.BAD_REQUEST),
        INVALID_USERNAME(209, "Invalid username, username must be between %d and %d characters",
                        HttpStatus.BAD_REQUEST),
        INVALID_PASSWORD(210,
                        "Invalid password, password must be between %d and %d characters, contain at least one uppercase letter, one lowercase letter, one number, and one special character",
                        HttpStatus.BAD_REQUEST),
        INVALID_EMAIL(211, "Invalid email", HttpStatus.BAD_REQUEST),
        INVALID_DOB(212, "Invalid date of birth, you must be at least 18 years old and at most 100 years old",
                        HttpStatus.BAD_REQUEST);

        int code;
        String message;
        HttpStatus status;
}
