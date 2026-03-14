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

public class RegisterUtil {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "user";
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final UserRole ROLE = UserRole.USER;

    public static RegisterUserCommand 회원생성_커맨드_생성() {
        return new RegisterUserCommand(EMAIL, PASSWORD, NAME);
    }

    public static User 커맨드기반_회원_생성(RegisterUserCommand command) {
        return User.create(
            command.email(),
            passwordEncoder.encode(command.password()),
            command.name()
        );
    }

    public static User 저장된_회원_생성() {
        return User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .name(NAME)
                .status(STATUS)
                .role(ROLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static RegisterUserResult 예상_회원생성_결과() {
        return RegisterUserResult.toDomain(저장된_회원_생성());
    }

    public static String 비밀번호_암호화(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}