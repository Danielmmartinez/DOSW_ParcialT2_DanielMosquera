package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.response.SolicitudAdoptadaResponse;
import edu.dosw.parcial.controller.mappers.SolicitudMapper;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.services.Impl.SolicitudServiceImpl;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import edu.dosw.parcial.persistence.document.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.mappers.MascotaEntityMapper;
import edu.dosw.parcial.persistence.mappers.SolicitudEntityMapper;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceConsultarAdoptadasTest {

    @Mock private MascotaRepository mascotaRepository;
    @Mock private SolicitudRepository solicitudRepository;
    @Mock private MascotaEntityMapper mascotaEntityMapper;

    private SolicitudServiceImpl solicitudService;

    @BeforeEach
    void setUp() {
        // Mappers reales: así las pruebas verifican también el contenido de la respuesta
        solicitudService = new SolicitudServiceImpl(
                mascotaRepository,
                solicitudRepository,
                mascotaEntityMapper,
                Mappers.getMapper(SolicitudEntityMapper.class),
                Mappers.getMapper(SolicitudMapper.class));
    }

    private SolicitudAdopcionDocument solicitud(String id, String usuarioId, String mascotaId, LocalDate fechaRecibimiento) {
        return SolicitudAdopcionDocument.builder()
                .id(id)
                .usuarioId(usuarioId)
                .mascotaId(mascotaId)
                .fechaSolicitud(LocalDateTime.now())
                .fechaRecibimiento(fechaRecibimiento)
                .estado(EstadoSolicitud.APROBADA)
                .tieneNinos(true)
                .tieneOtrasMascotas(false)
                .tieneEspacioReducido(false)
                .build();
    }

    private MascotaDocument mascota(String id, EstadoMascota estado) {
        return MascotaDocument.builder().id(id).nombre("Max").estadoMascota(estado).build();
    }

    @Test
    @DisplayName("ADOPTANTE: consulta solo sus propias solicitudes aprobadas")
    void consultarAdoptadas_adoptante_soloConsultaSusSolicitudes() {
        // Arrange
        when(solicitudRepository.findByUsuarioIdAndEstadoOrderByFechaRecibimientoDesc("u1", EstadoSolicitud.APROBADA))
                .thenReturn(List.of(solicitud("SOL-1", "u1", "PERRO-001", LocalDate.now().plusDays(5))));
        when(mascotaRepository.findAllById(anyList()))
                .thenReturn(List.of(mascota("PERRO-001", EstadoMascota.EN_PROCESO)));

        // Act
        List<SolicitudAdoptadaResponse> resultado = solicitudService.consultarAdoptadas("u1", Rol.ADOPTANTE);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("PERRO-001", resultado.get(0).getIdMascota());
        assertEquals("EN_PROCESO", resultado.get(0).getEstadoMascota());
        assertEquals(LocalDate.now().plusDays(5), resultado.get(0).getFechaRecibimiento());
        assertTrue(resultado.get(0).getTieneNinos());
        assertFalse(resultado.get(0).getTieneOtrasMascotas());
        assertFalse(resultado.get(0).getTieneEspacioReducido());
        verify(solicitudRepository, never()).findByEstadoOrderByFechaRecibimientoDesc(any());
    }

    @Test
    @DisplayName("ADMIN: consulta las solicitudes aprobadas de todos los usuarios")
    void consultarAdoptadas_admin_consultaTodas() {
        // Arrange
        when(solicitudRepository.findByEstadoOrderByFechaRecibimientoDesc(EstadoSolicitud.APROBADA))
                .thenReturn(List.of(
                        solicitud("SOL-1", "u1", "PERRO-001", LocalDate.now().plusDays(5)),
                        solicitud("SOL-2", "u2", "GATO-001", LocalDate.now().plusDays(3))));
        when(mascotaRepository.findAllById(anyList()))
                .thenReturn(List.of(
                        mascota("PERRO-001", EstadoMascota.EN_PROCESO),
                        mascota("GATO-001", EstadoMascota.ADOPTADA)));

        // Act
        List<SolicitudAdoptadaResponse> resultado = solicitudService.consultarAdoptadas("admin-id", Rol.ADMIN);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("EN_PROCESO", resultado.get(0).getEstadoMascota());
        assertEquals("ADOPTADA", resultado.get(1).getEstadoMascota());
        verify(solicitudRepository, never())
                .findByUsuarioIdAndEstadoOrderByFechaRecibimientoDesc(anyString(), any());
    }

    @Test
    @DisplayName("Conserva el orden descendente por fecha que entrega el repositorio")
    void consultarAdoptadas_conservaOrdenDescendente() {
        // Arrange
        when(solicitudRepository.findByEstadoOrderByFechaRecibimientoDesc(EstadoSolicitud.APROBADA))
                .thenReturn(List.of(
                        solicitud("SOL-1", "u1", "A", LocalDate.now().plusDays(10)),
                        solicitud("SOL-2", "u2", "B", LocalDate.now().plusDays(5)),
                        solicitud("SOL-3", "u3", "C", LocalDate.now().plusDays(2))));
        when(mascotaRepository.findAllById(anyList()))
                .thenReturn(List.of(
                        mascota("A", EstadoMascota.EN_PROCESO),
                        mascota("B", EstadoMascota.EN_PROCESO),
                        mascota("C", EstadoMascota.EN_PROCESO)));

        // Act
        List<SolicitudAdoptadaResponse> resultado = solicitudService.consultarAdoptadas("admin-id", Rol.ADMIN);

        // Assert
        assertEquals(List.of("A", "B", "C"),
                resultado.stream().map(SolicitudAdoptadaResponse::getIdMascota).toList());
    }

    @Test
    @DisplayName("E11: sin solicitudes aprobadas devuelve lista vacía y no consulta mascotas")
    void consultarAdoptadas_sinResultados_devuelveListaVacia() {
        // Arrange
        when(solicitudRepository.findByUsuarioIdAndEstadoOrderByFechaRecibimientoDesc("u1", EstadoSolicitud.APROBADA))
                .thenReturn(List.of());

        // Act
        List<SolicitudAdoptadaResponse> resultado = solicitudService.consultarAdoptadas("u1", Rol.ADOPTANTE);

        // Assert
        assertTrue(resultado.isEmpty());
        verifyNoInteractions(mascotaRepository);
    }

    @Test
    @DisplayName("Si la mascota ya no existe, la solicitud se lista con estadoMascota nulo")
    void consultarAdoptadas_mascotaInexistente_estadoNulo() {
        // Arrange
        when(solicitudRepository.findByUsuarioIdAndEstadoOrderByFechaRecibimientoDesc("u1", EstadoSolicitud.APROBADA))
                .thenReturn(List.of(solicitud("SOL-1", "u1", "FANTASMA", LocalDate.now().plusDays(5))));
        when(mascotaRepository.findAllById(anyList())).thenReturn(List.of());

        // Act
        List<SolicitudAdoptadaResponse> resultado = solicitudService.consultarAdoptadas("u1", Rol.ADOPTANTE);

        // Assert
        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).getEstadoMascota());
    }
}
