package com.example.auth_security_system.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private final UUID id;

    private final String email;
    private String password;
    private String name;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User create(String email, String password, String name) {
        return User.builder()
                .id(null)
                .email(email)
                .password(password)
                .name(name)
                .status(UserStatus.ACTIVE)
                .role(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    public void validateUserStatus() {
        switch (this.status) {
            case UserStatus.SLEEP:
                throw new IllegalStateException("휴면 상태의 사용자입니다.");
            case UserStatus.BANNED:
                throw new IllegalStateException("차단된 사용자입니다.");
            default:
                break;
        }
    }

    public void updatePassword(String newEncodedPassword) {
        if (newEncodedPassword == null || newEncodedPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        this.password = newEncodedPassword;
    }
}
