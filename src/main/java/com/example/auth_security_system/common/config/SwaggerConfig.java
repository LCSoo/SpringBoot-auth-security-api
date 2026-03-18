package com.example.auth_security_system.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // JWT 보안 스키마 정의
        String jwtSchemaName = "jwtAuth";
        SecurityScheme securityScheme = new SecurityScheme()
            .name(jwtSchemaName)
            .type(SecurityScheme.Type.HTTP) // HTTP 인증 방식 사용
            .scheme("bearer")   // Bearer 토큰 방식 사용
            .bearerFormat("JWT"); // JWT 토큰 형식 명시

        // 모든 API 요청에 위해서 정의한 보안 스키마를 적용하도록 요구
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemaName);

        // OpenAPI 객체 생성 및 반환
        return new OpenAPI()
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(new Components().addSecuritySchemes(jwtSchemaName, securityScheme));
    }

    private Info apiInfo() {
        return new Info()
            .title("회원 인증/인가 API 명세서")
            .description("헥사고날 아키텍처 기반의 Spring Boot 3 JWT 인증 시스템 API입니다.")
            .version("v1.0.0");
    }
}