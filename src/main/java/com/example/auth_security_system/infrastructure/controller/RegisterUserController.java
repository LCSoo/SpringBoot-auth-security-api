package com.example.auth_security_system.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.auth_security_system.application.usecase.RegisterUserUseCase;
import com.example.auth_security_system.infrastructure.dto.ApiResponse;
import com.example.auth_security_system.infrastructure.dto.RegisterUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원가입 API", description = "사용자 회원가입을 담당합니다.")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class RegisterUserController {

    private final RegisterUserUseCase registerUserUseCase;


    @Operation(summary = "사용자 회원가입", description = "이메일, 비밀번호, 이름을 입력하여 새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request) {

        var result = registerUserUseCase.register(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "회원 가입에 성공했습니다."));
    }
}