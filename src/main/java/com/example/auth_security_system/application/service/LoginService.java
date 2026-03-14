package com.example.auth_security_system.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.auth_security_system.application.dto.JwtTokenResult;
import com.example.auth_security_system.application.dto.LoginCommand;
import com.example.auth_security_system.application.port.CreateJwtTokenPort;
import com.example.auth_security_system.application.port.LoadUserPort;
import com.example.auth_security_system.application.usecase.LoginUseCase;
import com.example.auth_security_system.domain.model.UserInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase{

    private final LoadUserPort loadUserPort;
    private final CreateJwtTokenPort createJwtTokenPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtTokenResult login(LoginCommand command) {
        // 회원 조회
        var user = loadUserPort.findByEmail(command.email())
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 도메인 검증
        user.validateUserStatus();

        // 비밀번호 검증
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 발급
        UserInfo userInfo = UserInfo.of(user);
        String accessToken = createJwtTokenPort.createAccessToken(userInfo);
        String refreshToken = createJwtTokenPort.createRefreshToken(userInfo);

        return new JwtTokenResult(accessToken, refreshToken);
    }
}