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
    UNCATEGORIZED(000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatus status;
}
