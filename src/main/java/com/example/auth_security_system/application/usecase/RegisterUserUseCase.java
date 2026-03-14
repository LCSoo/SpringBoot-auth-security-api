package com.example.auth_security_system.application.usecase;

import com.example.auth_security_system.application.dto.RegisterUserCommand;
import com.example.auth_security_system.application.dto.RegisterUserResult;

public interface RegisterUserUseCase {
    RegisterUserResult register(RegisterUserCommand command);
}