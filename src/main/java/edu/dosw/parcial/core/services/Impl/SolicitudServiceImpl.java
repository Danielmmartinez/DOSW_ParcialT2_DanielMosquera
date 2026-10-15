package edu.dosw.parcial.core.services.Impl;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.controller.dtos.response.SolicitudAdoptadaResponse;
import edu.dosw.parcial.controller.handlers.exceptions.*;
import edu.dosw.parcial.controller.mappers.SolicitudMapper;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.models.SolicitudAdopcion;
import edu.dosw.parcial.core.services.SolicitudService;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import edu.dosw.parcial.persistence.document.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.mappers.MascotaEntityMapper;
import edu.dosw.parcial.persistence.mappers.SolicitudEntityMapper;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final MascotaRepository mascotaRepository;
    private final SolicitudRepository solicitudRepository;
    private final MascotaEntityMapper mascotaEntityMapper;
    private final SolicitudEntityMapper solicitudEntityMapper;
    private final SolicitudMapper solicitudMapper;

    @Override
    @Transactional
    public CrearSolicitudResponse adoptar(String usuarioId, CrearSolicitudRequest request) {
        log.info("Procesando adopción: usuarioId={}, mascotaId={}", usuarioId, request.getIdMascota());

        // (404)
        MascotaDocument mascotaDoc = mascotaRepository.findById(request.getIdMascota())
                .orElseThrow(() -> {
                    log.warn("Mascota no encontrada id={}", request.getIdMascota());
                    return new MascotaNoEncontradaException("La mascota solicitada no existe");
                });

        Mascota mascota = mascotaEntityMapper.toDomain(mascotaDoc);

        //  (409 - E06)
        if (!mascota.estaDisponible()) {
            log.warn("Mascota id={} no está disponible. Estado actual={}", mascota.getId(), mascota.getEstadoMascota());
            throw new MascotaNoDisponibleException("La mascota no está disponible para adopción");
        }

        // (409 - E8)
        if (solicitudRepository.existsByUsuarioIdAndEstado(usuarioId, EstadoSolicitud.APROBADA)) {
            log.warn("Adoptante usuarioId={} ya tiene una solicitud activa aprobada", usuarioId);
            throw new SegundaSolicitudException("El adoptante ya tiene una solicitud aprobada activa");
        }

        // (422 - E04)
        if (Boolean.TRUE.equals(request.getTieneNinos()) && !mascota.isCompatibleNinos()) {
            log.warn("Incompatibilidad: el hogar tiene niños y la mascota no es compatible con niños");
            throw new AdopcionIncompatibleException("La mascota no es compatible con hogares con niños");
        }
        if (Boolean.TRUE.equals(request.getTieneOtrasMascotas()) && !mascota.isCompatibleMascotas()) {
            log.warn("Incompatibilidad: el hogar tiene mascotas y la mascota no es compatible con otras mascotas");
            throw new AdopcionIncompatibleException("La mascota no es compatible con otras mascotas");
        }
        if (Boolean.TRUE.equals(request.getTieneEspacioReducido()) && !mascota.isCompatibleEspaciosReducidos()) {
            log.warn("Incompatibilidad: vivienda reducida y la mascota requiere espacio amplio");
            throw new AdopcionIncompatibleException("La mascota no es compatible con espacios reducidos");
        }

        // (422 - E07)
        LocalDate hoy = LocalDate.now();
        LocalDate minFecha = hoy.plusDays(1);
        LocalDate maxFecha = hoy.plusDays(15);

        if (request.getFechaRecibimiento().isBefore(minFecha) || request.getFechaRecibimiento().isAfter(maxFecha)) {
            log.warn("Fecha de recibimiento fuera de rango: {}. Permitida entre {} y {}",
                    request.getFechaRecibimiento(), minFecha, maxFecha);
            throw new FechaRecibimientoInvalidaException("La fecha de recibimiento debe estar entre 1 y 15 días a partir de hoy");
        }

        mascota.marcarEnProceso();
        mascotaRepository.save(mascotaEntityMapper.toDocument(mascota));

        SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                .usuarioId(usuarioId)
                .mascotaId(mascota.getId())
                .fechaSolicitud(LocalDateTime.now())
                .fechaRecibimiento(request.getFechaRecibimiento())
                .estado(EstadoSolicitud.APROBADA)
                .aplazada(false)
                .tieneNinos(request.getTieneNinos())
                .tieneOtrasMascotas(request.getTieneOtrasMascotas())
                .tieneEspacioReducido(request.getTieneEspacioReducido())
                .build();

        SolicitudAdopcionDocument savedSolicitud = solicitudRepository.save(solicitudEntityMapper.toDocument(solicitud));
        log.info("Solicitud creada con éxito id={}, estado=APROBADA, mascotaId={} pasó a EN_PROCESO",
                savedSolicitud.getId(), mascota.getId());

        return solicitudMapper.toResponseDTO(solicitudEntityMapper.toDomain(savedSolicitud));
    }

    @Override 
    public List<SolicitudAdoptadaResponse> consultarAdoptadas(String usuarioId, Rol rol){

        log.info("Consultando mascotas adoptadas: usuarioId={}, rol={}", usuarioId, rol);

        List<SolicitudAdopcionDocument> solicitudes = Rol.ADMIN.equals(rol) ? solicitudRepository.findByEstadoOrderByFechaRecibimientoDesc(EstadoSolicitud.APROBADA) : solicitudRepository.findByUsuarioIdAndEstadoOrderByFechaRecibimientoDesc(usuarioId, EstadoSolicitud.APROBADA);

        //(200 - E11) sin resultados : lista vacia
        if (solicitudes.isEmpty()){
            log.info("Consulta de adoptadas sin resultados para usuarioId={}, rol={}", usuarioId, rol);
            return List.of();
        }

        // Una sola consulta para traer el estado actual de todas las mascotas involucradas
        List<String> mascotaIds = solicitudes.stream().map(SolicitudAdopcionDocument::getMascotaId).distinct().toList();
        Map<String, EstadoMascota> estadoPorMascota = new HashMap<>();
        for(MascotaDocument mascota : mascotaRepository.findAllById(mascotaIds)){
            estadoPorMascota.put(mascota.getId(), mascota.getEstadoMascota());
        }

        List<SolicitudAdoptadaResponse> respuesta = solicitudes.stream().map(doc -> solicitudMapper.toAdoptadaResponse(solicitudEntityMapper.toDomain(doc), estadoPorMascota.get(doc.getMascotaId()))).toList();

        log.info("Consulta de adoptadas exitosa: {} resultado(s)", respuesta.size());
        return respuesta;
    }
}