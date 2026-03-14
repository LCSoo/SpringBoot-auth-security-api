package com.example.auth_security_system.util;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.auth_security_system.application.dto.RegisterUserCommand;
import com.example.auth_security_system.application.dto.RegisterUserResult;
import com.example.auth_security_system.domain.model.User;
import com.example.auth_security_system.domain.model.UserRole;
import com.example.auth_security_system.domain.model.UserStatus;
import com.example.auth_security_system.infrastructure.dto.RegisterUserRequest;

public class RegisterUtil {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password1234";
    private static final String NAME = "user";
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final UserRole ROLE = UserRole.USER;

    public static RegisterUserRequest 회원등록_요청_생성() {
        return new RegisterUserRequest(EMAIL, PASSWORD, NAME);
    }

    public static RegisterUserCommand 커맨드_생성() {
        var request = 회원등록_요청_생성();
        return new RegisterUserCommand(request.email(), request.password(), request.name());
    }

    public static User 회원_생성() {
        var command = 커맨드_생성();
        return User.create(command.email(), passwordEncoder.encode(command.password()), command.name());
    }

    public static User 저장된_회원_생성() {
        var command = 커맨드_생성();
        return User.builder()
                .id(USER_ID)
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .name(command.name())
                .status(STATUS)
                .role(ROLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static RegisterUserResult 예상_회원생성_결과() {
        var savedUser = 저장된_회원_생성();
        return RegisterUserResult.toDomain(savedUser);
    }
}