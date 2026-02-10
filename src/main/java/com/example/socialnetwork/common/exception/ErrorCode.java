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
        // Common/System errors: 1xxx
        UNCATEGORIZED(1000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
        INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
        // Identity Module: 2xxx
        USERNAME_EXIST(2001, "Username already exists", HttpStatus.BAD_REQUEST),
        ROLE_NOT_FOUND(2002, "Role not found", HttpStatus.BAD_REQUEST),
        USER_NOT_FOUND(2003, "User not found", HttpStatus.BAD_REQUEST),
        PASSWORD_INCORRECT(2004, "Password incorrect", HttpStatus.BAD_REQUEST),
        UNAUTHENTICATED(2005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
        OTP_EXPIRED(2006, "OTP expired", HttpStatus.BAD_REQUEST),
        INVALID_OTP(2007, "Invalid OTP", HttpStatus.BAD_REQUEST),
        PASSWORD_NOT_MATCH(2008, "Password not match", HttpStatus.BAD_REQUEST),
        INVALID_USERNAME(2009, "Invalid username, username must be between %d and %d characters",
                        HttpStatus.BAD_REQUEST),
        INVALID_PASSWORD(2010,
                        "Invalid password, password must be between %d and %d characters, contain at least one uppercase letter, one lowercase letter, one number, and one special character",
                        HttpStatus.BAD_REQUEST),
        INVALID_EMAIL(2011, "Invalid email", HttpStatus.BAD_REQUEST),
        INVALID_DOB(2012, "Invalid date of birth, you must be at least 18 years old and at most 100 years old",
                        HttpStatus.BAD_REQUEST),
        TOO_MANY_REQUESTS(2013, "Too many requests, please try again later", HttpStatus.TOO_MANY_REQUESTS),
        ROLE_EXIST(2014,"Role already exists" , HttpStatus.BAD_REQUEST);

        int code;
        String message;
        HttpStatus status;
}
