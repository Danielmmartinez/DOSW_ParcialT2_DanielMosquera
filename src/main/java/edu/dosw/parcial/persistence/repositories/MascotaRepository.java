package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends MongoRepository<MascotaDocument, String> {
    List<MascotaDocument> findByEstadoMascota(EstadoMascota estado);
    long countByEspecie(edu.dosw.parcial.core.models.Especie especie);
}