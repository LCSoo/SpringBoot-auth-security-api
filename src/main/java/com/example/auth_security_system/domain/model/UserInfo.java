package com.example.auth_security_system.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserInfo {
    private String id;
    private String email;
    private String name;
    private String role;

    public static UserInfo of(User user) {
        return UserInfo.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getRole().name())
            .build();
    }
}