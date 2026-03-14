package com.example.auth_security_system.infrastructure.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.example.auth_security_system.application.port.LoadUserPort;
import com.example.auth_security_system.application.port.SaveUserPort;
import com.example.auth_security_system.domain.model.User;
import com.example.auth_security_system.infrastructure.entity.UserEntity;
import com.example.auth_security_system.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter implements LoadUserPort, SaveUserPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public Optional<User> saveUser(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        UserEntity savedEntity =  userRepository.save(entity);

        return Optional.of(savedEntity.toDomain());
    }
}