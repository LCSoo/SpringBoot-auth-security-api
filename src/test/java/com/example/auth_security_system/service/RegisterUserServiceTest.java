package com.example.auth_security_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.auth_security_system.application.dto.RegisterUserResult;
import com.example.auth_security_system.application.port.LoadUserPort;
import com.example.auth_security_system.application.port.SaveUserPort;
import com.example.auth_security_system.application.service.RegisterUserService;
import com.example.auth_security_system.util.RegisterUtil;

@Import(RegisterUtil.class)
@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @InjectMocks
    RegisterUserService registerUserService;

    @Mock
    SaveUserPort saveUserPort;

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void 회원_생성_테스트() {
        var command = RegisterUtil.회원생성_커맨드_생성();
        var savedUser = RegisterUtil.저장된_회원_생성();

        var expectedResult = RegisterUtil.예상_회원생성_결과();

        when(saveUserPort.saveUser(any())).thenReturn(Optional.of(savedUser));
        when(loadUserPort.existsByEmail(any())).thenReturn(false);

        assertThat(registerUserService.register(command))
            .isNotNull()
            .isInstanceOf(RegisterUserResult.class)
            .extracting(
                "userId", "email", "name", "role"
            )
            .containsExactly(
                expectedResult.userId(),
                expectedResult.email(),
                expectedResult.name(),
                expectedResult.role()
            );
    }
}
