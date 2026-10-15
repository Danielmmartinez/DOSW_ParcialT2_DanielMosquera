package edu.dosw.parcial.config.seeders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.persistence.document.UsuarioDocument;
import edu.dosw.parcial.persistence.mappers.UsuarioEntityMapper;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;

@Component
@Profile("!test")
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class UsuarioSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEntityMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearSiNoExiste("admin@pawconnect.com", "admin123", "Administrador", Rol.ADMIN);
        crearSiNoExiste("juan@mail.com", "juan12345", "Juan Pérez", Rol.ADOPTANTE);
    }

    private void crearSiNoExiste(String correo, String password, String nombre, Rol rol) {
        if (usuarioRepository.existsByCorreo(correo)) {
            log.debug("Usuario ya existía: {}", correo);
            return;
        }

        Usuario usuario = Usuario.builder()
                .correo(correo)
                .password(passwordEncoder.encode(password))
                .nombre(nombre)
                .rol(rol)
                .build();

        UsuarioDocument doc = usuarioMapper.toDocument(usuario);
        usuarioRepository.save(doc);
        log.info("Usuario predefinido creado: correo={} rol={}", correo, rol);
    }
}