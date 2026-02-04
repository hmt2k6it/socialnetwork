package com.example.socialnetwork.common.exception;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import lombok.AccessLevel;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppException extends RuntimeException {
    ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
