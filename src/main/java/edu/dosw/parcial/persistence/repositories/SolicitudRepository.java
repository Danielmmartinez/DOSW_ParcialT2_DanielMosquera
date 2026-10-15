package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.document.SolicitudAdopcionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends MongoRepository<SolicitudAdopcionDocument, String> {
    boolean existsByUsuarioIdAndEstado(String usuarioId, EstadoSolicitud estado);
    List<SolicitudAdopcionDocument> findByEstadoOrderByFechaSolicitudDesc(EstadoSolicitud estado);
}