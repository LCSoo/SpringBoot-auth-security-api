package com.example.auth_security_system.application.service;

import org.springframework.stereotype.Service;
import com.example.auth_security_system.application.usecase.LogoutUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService implements LogoutUseCase{
    
    // 해당 서비스는 로그아웃 시 Redis에 저장된 리프레시 토큰을 삭제하는 역할을 수행한다.

    @Override
    public void logout() {
    }

    
}