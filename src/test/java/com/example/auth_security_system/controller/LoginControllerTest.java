package com.example.auth_security_system.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import com.example.auth_security_system.application.usecase.LoginUseCase;
import com.example.auth_security_system.application.usecase.LogoutUseCase;
import com.example.auth_security_system.domain.model.UserInfo;
import com.example.auth_security_system.infrastructure.controller.AuthController;
import com.example.auth_security_system.infrastructure.dto.LoginRequest;
import com.example.auth_security_system.util.RegisterUtil;
import com.example.auth_security_system.util.TestTokenUtil;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    LoginUseCase loginUseCase;

    @MockitoBean
    LogoutUseCase logoutUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private LoginRequest loginRequest;
    private UserInfo loginUserInfo;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password1234");
        loginUserInfo = RegisterUtil.저장된_회원_정보_생성();
    }

    @Test
    @DisplayName("로그인 성공 시, AccessToken => 헤더 | RefreshToken => 쿠키")
    void 로그인_테스트() throws Exception {
        var expectedCreatedToken = TestTokenUtil.유효한_토큰_생성(loginUserInfo);
        System.out.println("expectedCreatedToken = " + expectedCreatedToken);

        when(loginUseCase.login(any())).thenReturn(expectedCreatedToken);
        
        var resultAction = mockMvcTester.post()
            .uri("/api/v1/auth/login")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(loginRequest))
            .exchange();

        // 상태 코드 검증
        assertThat(resultAction)
            .apply(print())
            .hasStatus(200);

        // AccessToken 헤더 검증
        assertThat(resultAction)
            .hasHeader(HttpHeaders.AUTHORIZATION, "Bearer " + expectedCreatedToken.accessToken());

        // RefreshToken 쿠키 검증
        assertThat(resultAction)
            .cookies()
            .containsCookie("refreshToken")
            .isHttpOnly("refreshToken", true)
            .isSecure("refreshToken", true)
            .hasMaxAge("refreshToken", Duration.ofDays(14))
            .hasValue("refreshToken", expectedCreatedToken.refreshToken());
    }

    @Test
    @DisplayName("로그아웃 시, RefreshToken 쿠키 수명 0으로 수정")
    void 로그아웃_테스트() throws Exception {

        var resultAction = mockMvcTester.post()
            .uri("/api/v1/auth/logout")
            .exchange();

        // 상태 코드 검증
        assertThat(resultAction)
            .apply(print())
            .hasStatus(200);

        // RefreshToken 쿠키 검증
        assertThat(resultAction)
            .cookies()
            .containsCookie("refreshToken")
            .hasMaxAge("refreshToken", Duration.ZERO);
    }
}