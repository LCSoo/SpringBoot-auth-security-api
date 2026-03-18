package com.example.auth_security_system.application.dto;

public record JwtTokenResult(
    String accessToken,
    String refreshToken
) {
}
