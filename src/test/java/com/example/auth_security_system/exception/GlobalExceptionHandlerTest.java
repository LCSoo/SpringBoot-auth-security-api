package com.example.auth_security_system.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import com.example.auth_security_system.controller.TestFakeController;
import com.example.auth_security_system.controller.TestFakeController.DummyValidationRequest;
import com.example.auth_security_system.infrastructure.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;

@Import(GlobalExceptionHandler.class)
@WebMvcTest(controllers = TestFakeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    ObjectMapper objectMapper; 

    @Test
    @DisplayName("IllegalArgumentException 발생 시, => 400 Bad Request와 에러 JSON 반환")
    void IllegalArgumentException_400_테스트() throws Exception {
        var resultAction = mockMvcTester.get()
            .uri("/test/illegal-argument")
            .exchange();

        // 400 상태 코드 검증
        assertThat(resultAction)
            .apply(print())
            .hasStatus4xxClientError();

        // 응답 JSON 검증

        String responseBody = resultAction.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var json = objectMapper.readTree(responseBody);

        assertThat(json.get("status").asInt()).isEqualTo(400);
        assertThat(json.get("error").asString()).isEqualTo("Bad Request");
        assertThat(json.get("message").asString()).isEqualTo("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("@Valid 검증 실패 시, => 400 Bad Request와 첫 번째 에러 메시지 반환")
    void handleValidationException_400_테스트() throws Exception {
        var request = new DummyValidationRequest("");

        var resultAction = mockMvcTester.post()
            .uri("/test/validation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .exchange();

        assertThat(resultAction)
            .apply(print())
            .hasStatus4xxClientError();

        String responseBody = resultAction.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var json = objectMapper.readTree(responseBody);

        assertThat(json.get("status").asInt()).isEqualTo(400);
        assertThat(json.get("error").asString()).isEqualTo("Validation Failed");
        assertThat(json.get("message").asString()).isEqualTo("이름은 필수입니다.");
    }

    @Test
    @DisplayName("처리되지 않은 예외 발생 시, => 500 Internal Server Error와 관리자 문의 메시지 반환")
    void handleGenericException_500_테스트() throws Exception {
        var resultAction = mockMvcTester.get()
            .uri("/test/internal-error")
            .exchange();

        assertThat(resultAction)
            .apply(print())
            .hasStatus5xxServerError();

        String responseBody = resultAction.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var json = objectMapper.readTree(responseBody);

        assertThat(json.get("status").asInt()).isEqualTo(500);
        assertThat(json.get("error").asString()).isEqualTo("Internal Server Error");
        assertThat(json.get("message").asString()).isEqualTo("서버 내부에서 오류가 발생했습니다. 관리자에게 문의해주세요.");
    }
}