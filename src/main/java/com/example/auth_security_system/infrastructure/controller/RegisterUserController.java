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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class RegisterUserController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request) {

        var result = registerUserUseCase.register(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result, "회원 가입에 성공했습니다."));
    }
}