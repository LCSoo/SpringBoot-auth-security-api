package com.example.auth_security_system.util;

import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import com.example.auth_security_system.application.dto.JwtTokenResult;
import com.example.auth_security_system.domain.model.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TestTokenUtil {

    private static final String secret = "bWW/tgHlv2HjWisN1bl/F76i6tXYT8GdDnyCHP6LhVgBfiM6AC1VuktxiWHnQA73Kg27UiYlf3c0RK15zwxtvA==";
    private static final Duration accessTokenExpiration = Duration.ofMinutes(30);
    private static final Duration refreshTokenExpiration = Duration.ofDays(7);
    private static final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    public static JwtTokenResult 만료된_토큰_생성(UserInfo userInfo) {
        long now = (new Date()).getTime();
        String accessToken = Jwts.builder()
            .subject(userInfo.getId())
            .claim("category", "access")
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .claim("role", userInfo.getRole())
            .issuedAt(new Date(now - accessTokenExpiration.toMillis() - 1000))
            .expiration(new Date(now - 1000))
            .signWith(secretKey)
            .compact();

        String refreshToken = Jwts.builder()
            .subject(userInfo.getId())
            .claim("category", "refresh")
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .issuedAt(new Date(now - refreshTokenExpiration.toMillis() - 1000))
            .expiration(new Date(now - 1000))
            .signWith(secretKey)
            .compact();
        return new JwtTokenResult(accessToken, refreshToken);
    }

    public static JwtTokenResult 서명또는_조작된_토큰_생성(UserInfo userInfo) {
        long now = (new Date()).getTime();
        String invalidSecretKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidPayload.invalidSignature";
        String accessToken = Jwts.builder()
            .subject(userInfo.getId())
            .claim("category", "access")
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .claim("role", userInfo.getRole())
            .issuedAt(new Date(now))
            .expiration(new Date(now + accessTokenExpiration.toMillis()))
            .signWith(Keys.hmacShaKeyFor(invalidSecretKey.getBytes()))
            .compact();

        String refreshToken = Jwts.builder()
            .subject(userInfo.getId())
            .claim("category", "refresh")
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .issuedAt(new Date(now))
            .expiration(new Date(now + refreshTokenExpiration.toMillis()))
            .signWith(Keys.hmacShaKeyFor(invalidSecretKey.getBytes()))
            .compact();
        return new JwtTokenResult(accessToken, refreshToken);
    }
}