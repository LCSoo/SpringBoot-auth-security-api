package com.example.auth_security_system.infrastructure.adapter;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import com.example.auth_security_system.application.port.CreateJwtTokenPort;
import com.example.auth_security_system.domain.model.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

@Component
public class JwtTokenAdapter implements CreateJwtTokenPort {

    private final SecretKey secretKey;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtTokenAdapter(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration}") Duration accessTokenExpiration,
        @Value("${jwt.refresh-token-expiration}") Duration refreshTokenExpiration) {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
            this.accessTokenExpiration = accessTokenExpiration;
            this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public String createAccessToken(UserInfo userInfo) {
        long now = (new Date()).getTime();
        return Jwts.builder()
            .subject(userInfo.getId())
            .claim("category", "access")
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .claim("role", userInfo.getRole())
            .issuedAt(new Date(now))
            .expiration(new Date(now + accessTokenExpiration.toMillis()))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public String createRefreshToken(UserInfo userInfo) {
        long now = (new Date()).getTime();
        return Jwts.builder()
            .subject(userInfo.getId())
            .claim("category", "refresh")
            .claim("email", userInfo.getEmail())
            .claim("name", userInfo.getName())
            .issuedAt(new Date(now))
            .expiration(new Date(now + refreshTokenExpiration.toMillis()))
            .signWith(secretKey)
            .compact();
    }

    // 토큰에서 클레임 추출
    private Claims parseClaims(String token) {
        String rawToken = token.contains("Bearer ") ? token.replace("Bearer ", "") : token;

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(rawToken)
            .getPayload();
    }

    // 토큰에서 인증 객체 추출
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        return new UsernamePasswordAuthenticationToken(
            userId, 
            "", 
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        String rawToken = token.contains("Bearer ") ? token.replace("Bearer ", "") : token;

        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 서명 또는 형식의 토큰
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 토큰
        } catch (IllegalArgumentException e) {
            // 잘못된 토큰
        }

        return false;
    }
}