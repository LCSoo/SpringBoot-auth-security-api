package com.example.auth_security_system.common.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.auth_security_system.common.filter.JwtLoginFilter;
import com.example.auth_security_system.infrastructure.adapter.JwtTokenAdapter;
import com.example.auth_security_system.infrastructure.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenAdapter jwtTokenAdapter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        http
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())   
            );

        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        http
            .addFilterBefore(
                new JwtLoginFilter(jwtTokenAdapter), 
                UsernamePasswordAuthenticationFilter.class
            );

        http
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())   
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 프론트엔드 도메인 설정
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://your-frontend-domain.com"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 HTTP 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));

        // 인증 정보 포함 허용 여부
        configuration.setAllowCredentials(true);

        // 프론트엔드에서 응답 헤더 읽도록 설정
        configuration.setExposedHeaders(List.of("Authorization"));

        // 모든 경로에 대한 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}