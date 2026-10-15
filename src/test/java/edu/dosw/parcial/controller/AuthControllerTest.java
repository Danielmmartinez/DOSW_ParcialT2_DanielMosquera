package edu.dosw.parcial.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.dosw.parcial.config.SecurityConfig;
import edu.dosw.parcial.config.security.JwtAuthFilter;
import edu.dosw.parcial.config.security.JwtUtil;
import edu.dosw.parcial.controller.dtos.request.LoginRequest;
import edu.dosw.parcial.controller.dtos.response.LoginResponse;
import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.controller.handlers.exceptions.CredencialesInvalidasException;
import edu.dosw.parcial.core.services.AuthService;
import edu.dosw.parcial.main.ParcialApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = ParcialApplication.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthFilter.class}) 
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private JwtUtil jwtUtil;

    @Test
    void login_exitoso_debeRetornar200ConToken() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .correo("juan@mail.com").password("123456").build();

        when(authService.login(any())).thenReturn(
                LoginResponse.builder()
                        .token("token-abc").rol("ADOPTANTE").nombre("Juan").build());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-abc"))
                .andExpect(jsonPath("$.rol").value("ADOPTANTE"));
    }

    @Test
    void login_conCredencialesInvalidas_debeRetornar401() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .correo("juan@mail.com").password("mala123").build();

        when(authService.login(any()))
                .thenThrow(new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"));
    }

    @Test
    void login_conCorreoInvalido_debeRetornar400() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .correo("no-es-un-correo").password("").build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}