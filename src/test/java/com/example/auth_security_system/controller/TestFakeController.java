package com.example.auth_security_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Hidden
@RestController
public class TestFakeController {

    @GetMapping("/test/illegal-argument")
    public void throwIllegalArgument() {
        // 비즈니스 로직 에러 시뮬레이션
        throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }

    @PostMapping("/test/validation")
    public void throwValidation(@Valid @RequestBody DummyValidationRequest request) {
        // @Valid에 의해 MethodArgumentNotValidException 발생 시뮬레이션
    }

    @GetMapping("/test/internal-error")
    public void throwException() throws Exception {
        // 예상치 못한 서버 에러 시뮬레이션
        throw new Exception("DB Connection Timeout");
    }

    public static record DummyValidationRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name
    ) {}
}

