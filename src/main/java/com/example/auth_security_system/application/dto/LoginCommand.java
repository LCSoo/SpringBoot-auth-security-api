package com.example.auth_security_system.application.dto;

public record LoginCommand(
    String email,
    String password
) {

}
