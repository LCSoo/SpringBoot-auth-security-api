package com.example.auth_security_system.infrastructure.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
    int status,
    String error,
    String message
) {
}