package com.example.auth_security_system.application.port;

import java.util.Optional;
import com.example.auth_security_system.domain.model.User;

public interface LoadUserPort {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}