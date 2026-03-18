package com.example.auth_security_system.infrastructure.dto;

import com.example.auth_security_system.application.dto.LoginCommand;

public record LoginRequest(
    String email,
    String password
) {
    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
