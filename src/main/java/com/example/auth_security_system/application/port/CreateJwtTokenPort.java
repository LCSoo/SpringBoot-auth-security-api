package com.example.auth_security_system.application.port;

import com.example.auth_security_system.domain.model.UserInfo;

public interface CreateJwtTokenPort {
    String createAccessToken(UserInfo userInfo);
    String createRefreshToken(UserInfo userInfo);
}