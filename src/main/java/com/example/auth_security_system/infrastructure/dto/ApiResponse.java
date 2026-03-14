package com.example.auth_security_system.infrastructure.dto;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
    String status,
    T data,
    String message
) {
    public static <T> ApiResponse<T> success(T data, String successMessage) {
        return new ApiResponse<> ("success", data, successMessage);
    }

    public static <T> ApiResponse<T> fail(String failMessage) {
        return new ApiResponse<> ("fail", null, failMessage);
    }

    public static <T> ApiResponse<T> error(String errorMessage) {
        return new ApiResponse<> ("error", null, errorMessage);
    }
}