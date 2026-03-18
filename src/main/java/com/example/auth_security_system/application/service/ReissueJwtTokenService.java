package com.example.auth_security_system.application.service;

import org.springframework.stereotype.Service;
import com.example.auth_security_system.application.dto.JwtTokenResult;
import com.example.auth_security_system.application.dto.ReissueTokenCommand;
import com.example.auth_security_system.application.port.CreateJwtTokenPort;
import com.example.auth_security_system.application.port.LoadUserPort;
import com.example.auth_security_system.application.usecase.ReissueTokenUseCase;
import com.example.auth_security_system.domain.model.User;
import com.example.auth_security_system.domain.model.UserInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReissueJwtTokenService implements ReissueTokenUseCase {

    private final CreateJwtTokenPort createJwtTokenPort;
    private final LoadUserPort loadUserPort;

    @Override
    public JwtTokenResult reissue(ReissueTokenCommand command) {
        String refreshToken = command.refreshToken();

        // 리프레시 토큰 유효성 검증
        if (!createJwtTokenPort.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        // 토큰에서 사용자 정보 추출
        String email = createJwtTokenPort.extractEmail(refreshToken);

        // 이메일 기반 사용자 조회 및 상태 검증
        User user = loadUserPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.validateUserStatus();

        // 새로운 토큰 발급
        String newAccessToken = createJwtTokenPort.createAccessToken(UserInfo.of(user));
        String newRefreshToken = createJwtTokenPort.createRefreshToken(UserInfo.of(user));

        return new JwtTokenResult(newAccessToken, newRefreshToken);
    }

    
}