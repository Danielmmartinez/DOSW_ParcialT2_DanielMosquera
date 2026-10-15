package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.controller.handlers.exceptions.*;
import edu.dosw.parcial.controller.mappers.SolicitudMapper;
import edu.dosw.parcial.core.models.*;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock private MascotaRepository mascotaRepository;
    @Mock private SolicitudRepository solicitudRepository;
    @Mock private MascotaEntityMapper mascotaEntityMapper;
    @Mock private SolicitudEntityMapper solicitudEntityMapper;
    @Mock private SolicitudMapper solicitudMapper;

    @InjectMocks private SolicitudServiceImpl solicitudService;

    private MascotaDocument mascotaDoc;
    private Mascota mascota;
    private CrearSolicitudRequest requestValida;

    @BeforeEach
    void setUp() {
        mascotaDoc = MascotaDocument.builder()
                .id("PERRO-001")
                .nombre("Max")
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .compatibleNinos(true)
                .compatibleMascotas(true)
                .compatibleEspaciosReducidos(true)
                .build();

        mascota = Mascota.builder()
                .id("PERRO-001")
                .nombre("Max")
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .compatibleNinos(true)
                .compatibleMascotas(true)
                .compatibleEspaciosReducidos(true)
                .build();

        requestValida = CrearSolicitudRequest.builder()
                .idMascota("PERRO-001")
                .fechaRecibimiento(LocalDate.now().plusDays(5))
                .tieneNinos(true)
                .tieneOtrasMascotas(true)
                .tieneEspacioReducido(true)
                .build();
    }

    @Test
    @DisplayName("Happy Path: Crea solicitud APROBADA y cambia mascota a EN_PROCESO")
    void adoptar_exitoso() {
        // Arrange
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(mascotaDoc));
        when(mascotaEntityMapper.toDomain(mascotaDoc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr-1", EstadoSolicitud.APROBADA)).thenReturn(false);

        SolicitudAdopcionDocument solDoc = SolicitudAdopcionDocument.builder().id("SOL-001").estado(EstadoSolicitud.APROBADA).build();
        SolicitudAdopcion solDomain = SolicitudAdopcion.builder().id("SOL-001").estado(EstadoSolicitud.APROBADA).build();

        when(solicitudEntityMapper.toDocument(any(SolicitudAdopcion.class))).thenReturn(solDoc);
        when(solicitudRepository.save(any())).thenReturn(solDoc);
        when(solicitudEntityMapper.toDomain(solDoc)).thenReturn(solDomain);
        when(solicitudMapper.toResponseDTO(solDomain)).thenReturn(new CrearSolicitudResponse("SOL-001", "APROBADA"));

        // Act
        CrearSolicitudResponse response = solicitudService.adoptar("usr-1", requestValida);

        // Assert
        assertNotNull(response);
        assertEquals("SOL-001", response.getIdSolicitud());
        assertEquals("APROBADA", response.getEstadoSolicitud());
        assertEquals(EstadoMascota.EN_PROCESO, mascota.getEstadoMascota());
        verify(mascotaRepository).save(any());
        verify(solicitudRepository).save(any());
    }

    @Test
    @DisplayName("E06: Mascota no disponible debe lanzar MascotaNoDisponibleException (409)")
    void adoptar_mascotaNoDisponible_debeLanzarConflicto() {
        // Arrange
        mascota.setEstadoMascota(EstadoMascota.EN_PROCESO);
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(mascotaDoc));
        when(mascotaEntityMapper.toDomain(mascotaDoc)).thenReturn(mascota);

        // Act & Assert
        assertThrows(MascotaNoDisponibleException.class, () -> solicitudService.adoptar("usr-1", requestValida));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E8: Adoptante con solicitud aprobada activa debe lanzar SegundaSolicitudException (409)")
    void adoptar_segundaSolicitud_debeLanzarConflicto() {
        // Arrange
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(mascotaDoc));
        when(mascotaEntityMapper.toDomain(mascotaDoc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr-1", EstadoSolicitud.APROBADA)).thenReturn(true);

        // Act & Assert
        assertThrows(SegundaSolicitudException.class, () -> solicitudService.adoptar("usr-1", requestValida));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E04: Incompatibilidad de niños debe lanzar AdopcionIncompatibleException (422)")
    void adoptar_incompatibleNinos_debeLanzarUnprocessable() {
        // Arrange
        mascota.setCompatibleNinos(false);
        requestValida.setTieneNinos(true);

        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(mascotaDoc));
        when(mascotaEntityMapper.toDomain(mascotaDoc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr-1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(AdopcionIncompatibleException.class, () -> solicitudService.adoptar("usr-1", requestValida));
        assertEquals(EstadoMascota.DISPONIBLE, mascota.getEstadoMascota());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E07: Fecha recibimiento anterior a 1 día o superior a 15 días lanza FechaRecibimientoInvalidaException (422)")
    void adoptar_fechaInvalida_debeLanzarUnprocessable() {
        // Arrange
        requestValida.setFechaRecibimiento(LocalDate.now().plusDays(20)); // supera 15 días

        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(mascotaDoc));
        when(mascotaEntityMapper.toDomain(mascotaDoc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr-1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(FechaRecibimientoInvalidaException.class, () -> solicitudService.adoptar("usr-1", requestValida));
        assertEquals(EstadoMascota.DISPONIBLE, mascota.getEstadoMascota());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("Verificación de Precedencia: Si mascota no está disponible e incompatible a la vez, se prioriza E06 sobre E04")
    void adoptar_precedenciaE06AntesDeE04() {
        // Arrange
        mascota.setEstadoMascota(EstadoMascota.ADOPTADA);
        mascota.setCompatibleNinos(false);
        requestValida.setTieneNinos(true);

        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(mascotaDoc));
        when(mascotaEntityMapper.toDomain(mascotaDoc)).thenReturn(mascota);

        // Act & Assert
        assertThrows(MascotaNoDisponibleException.class, () -> solicitudService.adoptar("usr-1", requestValida));
    }
}