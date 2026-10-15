package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.AplazarSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.AplazarSolicitudResponse;
import edu.dosw.parcial.controller.handlers.exceptions.FechaRecibimientoInvalidaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudAjenaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudNoAprobadaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudNoEncontradaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudYaAplazadaException;
import edu.dosw.parcial.controller.mappers.SolicitudMapper;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.core.services.Impl.SolicitudServiceImpl;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceAplazarTest {

    private static final String USUARIO = "u1";
    private static final String ID = "SOL-001";

    @Mock private MascotaRepository mascotaRepository;
    @Mock private SolicitudRepository solicitudRepository;
    @Mock private MascotaEntityMapper mascotaEntityMapper;

    private SolicitudServiceImpl solicitudService;
    private final LocalDate hoy = LocalDate.now();

    @BeforeEach
    void setUp() {
        solicitudService = new SolicitudServiceImpl(
                mascotaRepository,
                solicitudRepository,
                mascotaEntityMapper,
                Mappers.getMapper(SolicitudEntityMapper.class),
                Mappers.getMapper(SolicitudMapper.class));
    }

    private SolicitudAdopcionDocument solicitud(String usuarioId, EstadoSolicitud estado, boolean aplazada, LocalDate fechaRecibimiento) {
        return SolicitudAdopcionDocument.builder()
                .id(ID)
                .usuarioId(usuarioId)
                .mascotaId("PERRO-001")
                .fechaSolicitud(LocalDateTime.now())
                .fechaRecibimiento(fechaRecibimiento)
                .estado(estado)
                .aplazada(aplazada)
                .build();
    }

    private AplazarSolicitudRequest request(LocalDate nuevaFecha) {
        return AplazarSolicitudRequest.builder().nuevaFechaRecibimiento(nuevaFecha).build();
    }

    @Test
    @DisplayName("Aplaza correctamente: actualiza fecha, marca aplazada y conserva estado APROBADA")
    void aplazar_exitoso_actualizaFechaYMarcaAplazada() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, false, hoy.plusDays(3))));
        when(solicitudRepository.save(any(SolicitudAdopcionDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        AplazarSolicitudResponse respuesta = solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(10)));

        // Assert
        assertEquals(ID, respuesta.getIdSolicitud());
        assertEquals(hoy.plusDays(10), respuesta.getFechaNuevaRecibimiento());

        ArgumentCaptor<SolicitudAdopcionDocument> captor = ArgumentCaptor.forClass(SolicitudAdopcionDocument.class);
        verify(solicitudRepository).save(captor.capture());
        SolicitudAdopcionDocument guardada = captor.getValue();
        assertTrue(guardada.isAplazada());
        assertEquals(hoy.plusDays(10), guardada.getFechaRecibimiento());
        assertEquals(hoy, guardada.getFechaAplazamiento());
        assertEquals(EstadoSolicitud.APROBADA, guardada.getEstado());
    }

    @Test
    @DisplayName("Los 15 días se cuentan desde hoy: hoy + 15 es válido")
    void aplazar_fechaLimiteHoyMasQuince_esValida() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, false, hoy.plusDays(3))));
        when(solicitudRepository.save(any(SolicitudAdopcionDocument.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        AplazarSolicitudResponse respuesta = solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(15)));

        // Assert
        assertEquals(hoy.plusDays(15), respuesta.getFechaNuevaRecibimiento());
    }

    @Test
    @DisplayName("404: la solicitud no existe")
    void aplazar_solicitudInexistente_lanzaNoEncontrada() {
        // Arrange
        when(solicitudRepository.findById(ID)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(SolicitudNoEncontradaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(10))));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("403: la solicitud pertenece a otro adoptante (BOLA) y no se modifica")
    void aplazar_solicitudAjena_lanzaAjena() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud("otro-usuario", EstadoSolicitud.APROBADA, false, hoy.plusDays(3))));

        // Act + Assert
        assertThrows(SolicitudAjenaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(10))));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("409: la solicitud no está aprobada")
    void aplazar_solicitudNoAprobada_lanzaNoAprobada() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APLAZADA, false, hoy.plusDays(3))));

        // Act + Assert
        assertThrows(SolicitudNoAprobadaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(10))));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("E9 - 409: segundo aplazamiento, se conserva la fecha ya registrada")
    void aplazar_dobleAplazamiento_lanzaYaAplazada() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, true, hoy.plusDays(8))));

        // Act + Assert
        assertThrows(SolicitudYaAplazadaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(12))));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("422: la nueva fecha no es posterior a hoy")
    void aplazar_fechaNoPosteriorAHoy_lanzaFechaInvalida() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, false, hoy.minusDays(1))));

        // Act + Assert
        assertThrows(FechaRecibimientoInvalidaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy)));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("422: la nueva fecha supera 15 días contados desde hoy")
    void aplazar_fechaSuperaQuinceDias_lanzaFechaInvalida() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, false, hoy.plusDays(3))));

        // Act + Assert
        assertThrows(FechaRecibimientoInvalidaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(16))));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("422: la nueva fecha no es posterior a la fecha de recibimiento vigente")
    void aplazar_fechaNoPosteriorALaVigente_lanzaFechaInvalida() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, false, hoy.plusDays(10))));

        // Act + Assert
        assertThrows(FechaRecibimientoInvalidaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(5))));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    @DisplayName("Precedencia: ya aplazada + fecha inválida informa 409, no 422")
    void aplazar_variasReglasFallan_informaLaPrimeraDelOrden() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud(USUARIO, EstadoSolicitud.APROBADA, true, hoy.plusDays(8))));

        // Act + Assert
        assertThrows(SolicitudYaAplazadaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(40))));
    }

    @Test
    @DisplayName("Precedencia: solicitud ajena + ya aplazada informa 403, no 409")
    void aplazar_ajenaYaAplazada_informaAjena() {
        // Arrange
        when(solicitudRepository.findById(ID))
                .thenReturn(Optional.of(solicitud("otro-usuario", EstadoSolicitud.APROBADA, true, hoy.plusDays(8))));

        // Act + Assert
        assertThrows(SolicitudAjenaException.class,
                () -> solicitudService.aplazar(USUARIO, ID, request(hoy.plusDays(12))));
    }
}
