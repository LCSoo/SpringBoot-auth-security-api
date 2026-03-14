package com.example.auth_security_system.application.dto;


public record RegisterUserCommand(
    String email,
    String password,
    String name
) {

}
