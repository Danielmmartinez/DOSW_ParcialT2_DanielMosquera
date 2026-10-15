package edu.dosw.parcial.core.services.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.dosw.parcial.config.security.JwtUtil;
import edu.dosw.parcial.controller.dtos.request.LoginRequest;
import edu.dosw.parcial.controller.dtos.response.LoginResponse;
import edu.dosw.parcial.controller.handlers.exceptions.CredencialesInvalidasException;
import edu.dosw.parcial.controller.mappers.AuthMapper;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.services.AuthService;
import edu.dosw.parcial.persistence.mappers.UsuarioEntityMapper;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioEntityMapper entityMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request){
        log.info("Intento de login para correo={}", request.getCorreo());

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.warn("Login fallido: correo no registrado {}", request.getCorreo());
                    return new CredencialesInvalidasException("Correo o contraseña incorrectos");
                });
        
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())){
            log.warn("Login fallido: contrasena incorrecta para correo={}", request.getCorreo());
            throw new CredencialesInvalidasException("Correo o contrasena incorrectos");
        }

        String token = jwtUtil.generateToken(usuario.getId(), usuario.getCorreo(), usuario.getRol().name());

        log.info("Login exitoso para usuarioId={} rol={}", usuario.getId(), usuario.getRol());
        return authMapper.toResponseDTO(usuario, token);
    }
    
}
