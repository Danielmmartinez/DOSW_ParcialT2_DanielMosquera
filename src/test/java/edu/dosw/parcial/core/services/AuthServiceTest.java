package edu.dosw.parcial.core.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.dosw.parcial.config.security.JwtUtil;
import edu.dosw.parcial.controller.dtos.request.LoginRequest;
import edu.dosw.parcial.controller.dtos.response.LoginResponse;
import edu.dosw.parcial.controller.handlers.exceptions.CredencialesInvalidasException;
import edu.dosw.parcial.controller.mappers.AuthMapper;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.services.Impl.AuthServiceImpl;
import edu.dosw.parcial.persistence.document.UsuarioDocument;
import edu.dosw.parcial.persistence.mappers.UsuarioEntityMapper;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioEntityMapper entityMapper;
    @Mock private AuthMapper authMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthServiceImpl authService;

    private UsuarioDocument doc;
    private Usuario usuario;
    private LoginRequest request;

    @BeforeEach
    void setUp() {
        doc = UsuarioDocument.builder()
                .id("u1").correo("juan@mail.com").password("hashed")
                .nombre("Juan").rol(Rol.ADOPTANTE).build();

        usuario = Usuario.builder()
                .id("u1").correo("juan@mail.com").password("hashed")
                .nombre("Juan").rol(Rol.ADOPTANTE).build();

        request = LoginRequest.builder()
                .correo("juan@mail.com")
                .password("123456")
                .build();
    }

    @Test
    void login_conCredencialesValidas_debeRetornarToken() {
        // Arrange
        when(usuarioRepository.findByCorreo("juan@mail.com")).thenReturn(Optional.of(doc));
        when(entityMapper.toDomain(doc)).thenReturn(usuario);
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("u1", "juan@mail.com", "ADOPTANTE")).thenReturn("token-abc");
        when(authMapper.toResponseDTO(usuario, "token-abc"))
                .thenReturn(LoginResponse.builder()
                        .token("token-abc").rol("ADOPTANTE").nombre("Juan").build());

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("token-abc", response.getToken());
        assertEquals("ADOPTANTE", response.getRol());
        verify(passwordEncoder).matches("123456", "hashed");
    }

    @Test
    void login_conCorreoNoRegistrado_debeLanzarCredencialesInvalidas() {
        when(usuarioRepository.findByCorreo("juan@mail.com")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }

    @Test
    void login_conPasswordIncorrecta_debeLanzarCredencialesInvalidas() {
        when(usuarioRepository.findByCorreo("juan@mail.com")).thenReturn(Optional.of(doc));
        when(entityMapper.toDomain(doc)).thenReturn(usuario);
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }
}