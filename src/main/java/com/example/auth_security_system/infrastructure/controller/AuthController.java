package com.example.auth_security_system.infrastructure.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.auth_security_system.application.dto.JwtTokenResult;
import com.example.auth_security_system.application.usecase.LoginUseCase;
import com.example.auth_security_system.application.usecase.LogoutUseCase;
import com.example.auth_security_system.infrastructure.dto.ApiResponseEntity;
import com.example.auth_security_system.infrastructure.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 API", description = "로그인 및 로그아웃을 담당합니다.")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공 (헤더/쿠키에 토큰 반환)"),
        @ApiResponse(responseCode = "400", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 토큰 생성
        JwtTokenResult createdToken = loginUseCase.login(loginRequest.toCommand());

        // 리프레시 토큰을 HttpOnly 쿠키에 저장
        ResponseCookie refreshTokenCookie = setRefreshTokenInCookie(createdToken.refreshToken());

        // 액세스 토큰을 헤더에 저장
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + createdToken.accessToken())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponseEntity.success(null, "로그인을 성공했습니다."));
    }

    @Operation(summary = "사용자 로그아웃", description = "Refresh Token을 삭제하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String userId) {
        
        // Redis에 저장된 리프레시 토큰 삭제
        logoutUseCase.logout();

        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie().toString())
            .body(ApiResponseEntity.success(null, "로그아웃에 성공했습니다."));
    }
    

    private ResponseCookie setRefreshTokenInCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서만 전송
                .path("/api/v1/auth/reissue") // 리프레시 토큰이 필요한 엔드포인트에만 쿠키가 전송되도록 설정
                .maxAge(14 * 24 * 60 * 60) // 14일
                .sameSite("Strict") // CSRF 공격 방지
                .build();
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth/reissue")
                .maxAge(0) // 즉시 만료
                .sameSite("Strict")
                .build();
    }
    
}