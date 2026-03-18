package com.example.auth_security_system.infrastructure.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.auth_security_system.application.dto.JwtTokenResult;
import com.example.auth_security_system.application.dto.ReissueTokenCommand;
import com.example.auth_security_system.application.usecase.ReissueTokenUseCase;
import com.example.auth_security_system.infrastructure.dto.ApiResponseEntity;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@Hidden
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class JwtTokenController {

    private final ReissueTokenUseCase reissueTokenUseCase;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰이 존재하지 않습니다.");
        }

        // 토큰 재발급
        JwtTokenResult reissuedTokens = reissueTokenUseCase.reissue(new ReissueTokenCommand(refreshToken));

        // 리프레시 토큰을 쿠키에 덮어쓰기
        ResponseCookie newRefreshTokenCookie = setRefreshTokenInCookie(reissuedTokens.refreshToken());

        // 액세스 토큰과 새로운 리프레시 토큰을 응답에 포함하여 반환
        return ResponseEntity.status(HttpStatus.OK)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokens.accessToken())
            .header(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString())
            .body(ApiResponseEntity.success(null, "토큰이 재발급되었습니다."));
    }

    private ResponseCookie setRefreshTokenInCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken",refreshToken)
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서만 전송
                .path("/api/v1/auth/reissue") // 리프레시 토큰이 필요한 엔드포인트에만 쿠키가 전송되도록 설정
                .maxAge(14 * 24 * 60 * 60) // 14일
                .sameSite("Strict") // CSRF 공격 방지
                .build();
    }
}