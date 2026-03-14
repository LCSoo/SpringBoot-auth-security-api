package com.example.auth_security_system.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import com.example.auth_security_system.TestcontainersConfiguration;
import com.example.auth_security_system.common.config.JpaAuditingConfig;
import com.example.auth_security_system.domain.model.User;
import com.example.auth_security_system.infrastructure.adapter.UserAdapter;
import com.example.auth_security_system.infrastructure.repository.UserRepository;
import com.example.auth_security_system.util.RegisterUtil;

@Import({UserAdapter.class, JpaAuditingConfig.class})
public class UserAdapterTest extends TestcontainersConfiguration {

    @Autowired
    UserAdapter userAdapter;

    @Autowired
    UserRepository userRepository;

    @Test
    void 회원_등록_테스트() {
        var user = RegisterUtil.회원_생성();
        var expected = RegisterUtil.저장된_회원_생성();

        assertThat(userAdapter.saveUser(user).get())
            .isNotNull()
            .isInstanceOf(User.class)
            .satisfies(savedUser -> {
                assertThat(savedUser.getId()).isNotNull();
                assertThat(savedUser.getEmail()).isEqualTo(expected.getEmail());
                assertThat(savedUser.getName()).isEqualTo(expected.getName());
                assertThat(savedUser.getRole()).isEqualTo(expected.getRole());
                assertThat(savedUser.getCreatedAt()).isAfter(expected.getCreatedAt());
            });
    }
}