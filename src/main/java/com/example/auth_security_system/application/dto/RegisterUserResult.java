package com.example.auth_security_system.application.dto;

import java.time.LocalDateTime;
import com.example.auth_security_system.domain.model.User;
import lombok.Builder;

@Builder
public record RegisterUserResult(
    String userId,
    String email,
    String name,
    String role,
    LocalDateTime createdAt
) {
    public static RegisterUserResult toDomain(User user) {
        return RegisterUserResult.builder()
            .userId(user.getId().toString())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getRole().name())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
