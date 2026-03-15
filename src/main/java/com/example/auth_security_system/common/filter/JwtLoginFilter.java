package com.example.auth_security_system.common.filter;

import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.auth_security_system.infrastructure.adapter.JwtTokenAdapter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtLoginFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenAdapter jwtTokenAdapter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 JWT 액세스 토큰 추출
        String accessToken = extractAccessToken(request);

        // 토큰 검증 및 인증 처리
        if (StringUtils.hasText(accessToken) && jwtTokenAdapter.validateToken(accessToken)) {
            // 토큰이 유효한 경우 => 인증 객체를 가져와 SecurityContext에 저장
            var authentication = jwtTokenAdapter.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        // 다음 필터로 요청 전달 (검증 실패 시 => 인증 객체 없이 다음 필터로 전달. 401 에러 발생시킴.)
        filterChain.doFilter(request, response);
    }

    // 헤더에서 JWT 액세스 토큰 추출
    private String extractAccessToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith(BEARER_PREFIX)) {
            return jwtToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}