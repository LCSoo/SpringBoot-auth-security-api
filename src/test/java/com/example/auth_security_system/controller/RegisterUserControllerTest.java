package com.example.auth_security_system.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import com.example.auth_security_system.application.usecase.RegisterUserUseCase;
import com.example.auth_security_system.infrastructure.controller.RegisterUserController;
import com.example.auth_security_system.infrastructure.dto.ApiResponseEntity;
import com.example.auth_security_system.util.RegisterUtil;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(RegisterUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegisterUserControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    RegisterUserUseCase registerUserUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

     @Test
     void 회원_등록_API_테스트() throws Exception {
        var request = RegisterUtil.회원등록_요청_생성();
        var expectedResult = RegisterUtil.예상_회원생성_결과();

        when(registerUserUseCase.register(any())).thenReturn(expectedResult);

        var resultAction = mockMvcTester.post()
            .uri("/api/v1/users/register")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(resultAction)
            .apply(print())
            .hasStatus(201)
            .bodyJson()
            .convertTo(ApiResponseEntity.class)
            .satisfies(res -> {
                assertThat(res.status()).isEqualTo("success");
                assertThat(res.data())
                    .extracting(
                        "email", "name", "role"
                    )
                    .containsExactly(
                        expectedResult.email(),
                        expectedResult.name(),
                        expectedResult.role()
                    );
            });
     }
}