package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.controller.handlers.exceptions.*;
import edu.dosw.parcial.controller.mappers.SolicitudMapper;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.SolicitudAdopcion;
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

    private MascotaDocument doc;
    private Mascota mascota;
    private CrearSolicitudRequest request;

    @BeforeEach
    void setUp() {
        doc = MascotaDocument.builder()
                .id("PERRO-001")
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .compatibleNinos(true)
                .compatibleMascotas(true)
                .compatibleEspaciosReducidos(true)
                .build();

        mascota = Mascota.builder()
                .id("PERRO-001")
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .compatibleNinos(true)
                .compatibleMascotas(true)
                .compatibleEspaciosReducidos(true)
                .build();

        request = CrearSolicitudRequest.builder()
                .idMascota("PERRO-001")
                .fechaRecibimiento(LocalDate.now().plusDays(5))
                .tieneNinos(true)
                .tieneOtrasMascotas(true)
                .tieneEspacioReducido(true)
                .build();
    }

    @Test
    @DisplayName("Arrange-Act-Assert: Adopción exitosa")
    void adoptar_exitoso() {
        // Arrange
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(false);

        SolicitudAdopcionDocument solDoc = SolicitudAdopcionDocument.builder().id("SOL-1").estado(EstadoSolicitud.APROBADA).build();
        SolicitudAdopcion solDomain = SolicitudAdopcion.builder().id("SOL-1").estado(EstadoSolicitud.APROBADA).build();

        when(solicitudEntityMapper.toDocument(any())).thenReturn(solDoc);
        when(solicitudRepository.save(any())).thenReturn(solDoc);
        when(solicitudEntityMapper.toDomain(solDoc)).thenReturn(solDomain);
        when(solicitudMapper.toResponseDTO(solDomain)).thenReturn(new CrearSolicitudResponse("SOL-1", "APROBADA"));

        // Act
        CrearSolicitudResponse response = solicitudService.adoptar("usr1", request);

        // Assert
        assertNotNull(response);
        assertEquals("SOL-1", response.getIdSolicitud());
        assertEquals("APROBADA", response.getEstadoSolicitud());
        assertEquals(EstadoMascota.EN_PROCESO, mascota.getEstadoMascota());
        verify(mascotaRepository).save(any());
        verify(solicitudRepository).save(any());
    }

    @Test
    @DisplayName("404: Mascota no existe")
    void adoptar_mascotaNoExiste() {
        // Arrange
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MascotaNoEncontradaException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E06 (409): Mascota no disponible")
    void adoptar_mascotaNoDisponible() {
        // Arrange
        mascota.setEstadoMascota(EstadoMascota.EN_PROCESO);
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);

        // Act & Assert
        assertThrows(MascotaNoDisponibleException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E8 (409): Adoptante con solicitud aprobada activa")
    void adoptar_segundaSolicitud() {
        // Arrange
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(true);

        // Act & Assert
        assertThrows(SegundaSolicitudException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E04 (422): Incompatibilidad con niños")
    void adoptar_incompatibleNinos() {
        // Arrange
        mascota.setCompatibleNinos(false);
        request.setTieneNinos(true);
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(AdopcionIncompatibleException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E04 (422): Incompatibilidad con otras mascotas")
    void adoptar_incompatibleOtrasMascotas() {
        // Arrange
        mascota.setCompatibleMascotas(false);
        request.setTieneOtrasMascotas(true);
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(AdopcionIncompatibleException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E04 (422): Incompatibilidad con espacio reducido")
    void adoptar_incompatibleEspacioReducido() {
        // Arrange
        mascota.setCompatibleEspaciosReducidos(false);
        request.setTieneEspacioReducido(true);
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(AdopcionIncompatibleException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E07 (422): Fecha de recibimiento menor a 1 día (hoy)")
    void adoptar_fechaMenorAUnDia() {
        // Arrange
        request.setFechaRecibimiento(LocalDate.now());
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(FechaRecibimientoInvalidaException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E07 (422): Fecha de recibimiento superior a 15 días")
    void adoptar_fechaMayorA15Dias() {
        // Arrange
        request.setFechaRecibimiento(LocalDate.now().plusDays(20));
        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);
        when(solicitudRepository.existsByUsuarioIdAndEstado("usr1", EstadoSolicitud.APROBADA)).thenReturn(false);

        // Act & Assert
        assertThrows(FechaRecibimientoInvalidaException.class, () -> solicitudService.adoptar("usr1", request));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("Precedencia de reglas: Mascota no disponible se evalúa antes que incompatibilidad")
    void adoptar_precedencia() {
        // Arrange
        mascota.setEstadoMascota(EstadoMascota.ADOPTADA);
        mascota.setCompatibleNinos(false);
        request.setTieneNinos(true);

        when(mascotaRepository.findById("PERRO-001")).thenReturn(Optional.of(doc));
        when(mascotaEntityMapper.toDomain(doc)).thenReturn(mascota);

        // Act & Assert
        assertThrows(MascotaNoDisponibleException.class, () -> solicitudService.adoptar("usr1", request));
    }
}