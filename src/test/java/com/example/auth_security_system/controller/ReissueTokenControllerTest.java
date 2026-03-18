package com.example.auth_security_system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import com.example.auth_security_system.application.usecase.ReissueTokenUseCase;
import com.example.auth_security_system.infrastructure.adapter.JwtTokenAdapter;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(JwtTokenAdapter.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReissueTokenControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @MockitoBean
    ReissueTokenUseCase reissueTokenUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper();

     @Test
     void 토큰_재발급_API_테스트() throws Exception {
        
     }
}