package com.example.auth_security_system.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.auth_security_system.infrastructure.adapter.JwtTokenAdapter;
import com.example.auth_security_system.util.RegisterUtil;
import com.example.auth_security_system.util.TestTokenUtil;

public class JwtTokenAdapterTest {
    private JwtTokenAdapter jwtTokenAdapter;

    private static final String secret = "bWW/tgHlv2HjWisN1bl/F76i6tXYT8GdDnyCHP6LhVgBfiM6AC1VuktxiWHnQA73Kg27UiYlf3c0RK15zwxtvA==";
    private static final Duration accessTokenExpiration = Duration.ofMinutes(30);
    private static final Duration refreshTokenExpiration = Duration.ofDays(7);

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter(
            secret, accessTokenExpiration, refreshTokenExpiration
        ); 
    }

    @Test
    void 액세스_및_리프레시_토큰_생성_테스트() {
        var userInfo = RegisterUtil.저장된_회원_정보_생성();

        assertThat(jwtTokenAdapter.createAccessToken(userInfo))
            .isNotNull()
            .isNotEmpty()
            .isInstanceOf(String.class);

        assertThat(jwtTokenAdapter.createRefreshToken(userInfo))
            .isNotNull()
            .isNotEmpty()
            .isInstanceOf(String.class);
    }

    @Test
    void 액세스_및_리프레시_토큰_검증_테스트() {
        var userInfo = RegisterUtil.저장된_회원_정보_생성();
        var accessToken = jwtTokenAdapter.createAccessToken(userInfo);
        var refreshToken = jwtTokenAdapter.createRefreshToken(userInfo);

        assertThat(jwtTokenAdapter.validateToken(accessToken))
            .isTrue();

        assertThat(jwtTokenAdapter.validateToken(refreshToken))
            .isTrue();
    }

    @Test
    @DisplayName("만료된 토큰을 검증하면 false를 반환한다.")
    void 만료된_토큰_검증_테스트() throws InterruptedException {
        var userInfo = RegisterUtil.저장된_회원_정보_생성();
        
        var expiredTokens = TestTokenUtil.만료된_토큰_생성(userInfo);

        assertThat(jwtTokenAdapter.validateToken(expiredTokens.accessToken()))
            .isFalse();

        assertThat(jwtTokenAdapter.validateToken(expiredTokens.refreshToken()))
            .isFalse();
    }

    @Test
    @DisplayName("잘못된 서명이나 조작된 토큰을 검증하면 false를 반환한다.")
    void 잘못된_토큰_검증_테스트() {
        var userInfo = RegisterUtil.저장된_회원_정보_생성();

        var invalidTokens = TestTokenUtil.서명또는_조작된_토큰_생성(userInfo);

        assertThat(jwtTokenAdapter.validateToken(invalidTokens.accessToken()))
            .isFalse();
        
        assertThat(jwtTokenAdapter.validateToken(invalidTokens.refreshToken()))
            .isFalse();
    }

    @Test
    @DisplayName("올바른 토큰에서 인증 객체를 추출해야 한다.")
    void 올바른_인증객체_추출_테스트() {
        var userInfo = RegisterUtil.저장된_회원_정보_생성();

        var accessToken = jwtTokenAdapter.createAccessToken(userInfo);

        // name = userId, authorities = role
        assertThat(jwtTokenAdapter.getAuthentication(accessToken))
            .isNotNull();
        assertThat(jwtTokenAdapter.getAuthentication(accessToken).getName())
            .isEqualTo(userInfo.getId());
        assertThat(jwtTokenAdapter.getAuthentication(accessToken).getAuthorities())
            .extracting("authority")
            .containsExactly(userInfo.getRole());
    }
}
