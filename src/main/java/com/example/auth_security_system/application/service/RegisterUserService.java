package com.example.auth_security_system.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.auth_security_system.application.dto.RegisterUserCommand;
import com.example.auth_security_system.application.dto.RegisterUserResult;
import com.example.auth_security_system.application.port.LoadUserPort;
import com.example.auth_security_system.application.port.SaveUserPort;
import com.example.auth_security_system.application.usecase.RegisterUserUseCase;
import com.example.auth_security_system.domain.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterUserResult register(RegisterUserCommand command) {
        // 이메일 중복 검증
        if (loadUserPort.existsByEmail(command.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(command.password());

        // 도메인 객체 생성
        User newUser = User.create(command.email(), encodedPassword, command.name());

        // DB 저장
        User savedUser = saveUserPort.saveUser(newUser)
            .orElseThrow(() -> new RuntimeException("사용자 저장에 실패했습니다."));

        return RegisterUserResult.toDomain(savedUser);
    }
}