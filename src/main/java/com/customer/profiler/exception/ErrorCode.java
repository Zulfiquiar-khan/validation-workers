package com.customer.profiler.exception;

public enum ErrorCode {
    VALIDATION_ERROR(),
    PROFILE_NOT_PRESENT;

    private int code;
    private int errorMessage;
}
