package com.example.auth_security_system.application.usecase;

import com.example.auth_security_system.application.dto.JwtTokenResult;
import com.example.auth_security_system.application.dto.LoginCommand;

public interface LoginUseCase {
    JwtTokenResult login(LoginCommand command);
}