package edu.dosw.parcial.core.services.Impl;

import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequest;
import edu.dosw.parcial.controller.dtos.response.CrearMascotaResponse;
import edu.dosw.parcial.controller.handlers.exceptions.DatosMascotaInvalidosException;
import edu.dosw.parcial.controller.handlers.exceptions.NodoDeOrigenInvalidoException;
import edu.dosw.parcial.controller.mappers.MascotaMapper;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.Tamano;
import edu.dosw.parcial.core.models.TipoNodo;
import edu.dosw.parcial.core.services.MascotaService;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import edu.dosw.parcial.persistence.document.NodoRefugioDocument;
import edu.dosw.parcial.persistence.mappers.MascotaEntityMapper;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.NodoRefugioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final NodoRefugioRepository nodoRefugioRepository;
    private final MascotaEntityMapper entityMapper;
    private final MascotaMapper mascotaMapper;

    @Override
    @Transactional
    public CrearMascotaResponse crearMascota(CrearMascotaRequest request) {
        log.info("Iniciando creación de mascota: nombre={}, especie={}", request.getNombre(), request.getEspecie());

        // (E02)
        Especie especie;
        Tamano tamano;
        try {
            especie = Especie.valueOf(request.getEspecie().toUpperCase());
            tamano = Tamano.valueOf(request.getTamano().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Especie o tamaño no admitido: especie={}, tamano={}", request.getEspecie(), request.getTamano());
            throw new DatosMascotaInvalidosException("La especie o el tamaño no corresponden a los valores permitidos");
        }

        // (E03)
        NodoRefugioDocument refugioDoc = nodoRefugioRepository.findById(request.getRefugioId())
                .orElseThrow(() -> {
                    log.warn("Refugio no encontrado id={}", request.getRefugioId());
                    return new NodoDeOrigenInvalidoException("El nodo de origen no existe");
                });

        if (!TipoNodo.REFUGIO.equals(refugioDoc.getTipo())) {
            log.warn("El nodo especificado id={} no es de tipo REFUGIO, es={}", refugioDoc.getId(), refugioDoc.getTipo());
            throw new NodoDeOrigenInvalidoException("La mascota debe pertenecer a un nodo de tipo REFUGIO");
        }

        long count = mascotaRepository.countByEspecie(especie);
        String idGenerado = String.format("%s-%03d", especie.name(), count + 1);

        Mascota mascota = mascotaMapper.toDomain(request);
        mascota.setId(idGenerado);
        mascota.setEspecie(especie);
        mascota.setTamano(tamano);
        mascota.setEstadoMascota(EstadoMascota.DISPONIBLE);

        MascotaDocument saved = mascotaRepository.save(entityMapper.toDocument(mascota));
        log.info("Mascota creada exitosamente con id={}", saved.getId());

        return mascotaMapper.toResponseDTO(entityMapper.toDomain(saved));
    }
}