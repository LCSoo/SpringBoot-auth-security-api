package com.example.auth_security_system.infrastructure.dto;

import lombok.Builder;

@Builder
public record ApiResponseEntity<T>(
    String status,
    T data,
    String message
) {
    public static <T> ApiResponseEntity<T> success(T data, String successMessage) {
        return new ApiResponseEntity<> ("success", data, successMessage);
    }

    public static <T> ApiResponseEntity<T> fail(String failMessage) {
        return new ApiResponseEntity<> ("fail", null, failMessage);
    }

    public static <T> ApiResponseEntity<T> error(String errorMessage) {
        return new ApiResponseEntity<> ("error", null, errorMessage);
    }
}