package com.example.auth_security_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.auth_security_system.application.dto.ReissueTokenCommand;
import com.example.auth_security_system.application.port.CreateJwtTokenPort;
import com.example.auth_security_system.application.port.LoadUserPort;
import com.example.auth_security_system.application.service.ReissueJwtTokenService;
import com.example.auth_security_system.util.RegisterUtil;
import com.example.auth_security_system.util.TestTokenUtil;

@ExtendWith(MockitoExtension.class)
public class ReissueTokenServiceTest {
    @InjectMocks
    ReissueJwtTokenService reissueJwtTokenService;
    
    @Mock
    CreateJwtTokenPort createJwtTokenPort;

    @Mock
    LoadUserPort loadUserPort;

    @Test
    void 토큰_재발급_테스트() throws InterruptedException {
        var userInfo = RegisterUtil.저장된_회원_정보_생성();
        var user = RegisterUtil.저장된_회원_생성();
        var tokens = TestTokenUtil.유효한_토큰_생성(userInfo);
        var command = new ReissueTokenCommand(tokens.refreshToken());

        Thread.sleep(5000); // 토큰 발급 시간 차이를 위해 5초 대기
        var newTokens = TestTokenUtil.유효한_토큰_생성(userInfo);

        when(createJwtTokenPort.validateToken(any())).thenReturn(true);
        when(createJwtTokenPort.extractEmail(any())).thenReturn(user.getEmail());
        when(loadUserPort.findByEmail(any())).thenReturn(Optional.of(user));
        when(createJwtTokenPort.createAccessToken(any())).thenReturn(newTokens.accessToken());
        when(createJwtTokenPort.createRefreshToken(any())).thenReturn(newTokens.refreshToken());

        assertThat(reissueJwtTokenService.reissue(command))
            .extracting("accessToken", "refreshToken")
            .containsExactly(newTokens.accessToken(), newTokens.refreshToken());

        verify(createJwtTokenPort, times(1)).validateToken(any());
        verify(createJwtTokenPort, times(1)).extractEmail(any());
        verify(loadUserPort, times(1)).findByEmail(any());
        verify(createJwtTokenPort, times(1)).createAccessToken(any());
        verify(createJwtTokenPort, times(1)).createRefreshToken(any());
    }
}