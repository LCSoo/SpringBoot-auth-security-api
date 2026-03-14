package com.example.auth_security_system.infrastructure.dto;

import com.example.auth_security_system.application.dto.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterUserRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
             message = "비밀번호는 8자 이상, 영문과 숫자를 포함해야 합니다.")
    String password,

    @NotBlank(message = "이름은 필수입니다.")
    String name
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(email, password, name);
    }
}