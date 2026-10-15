package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.document.NodoRefugioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NodoRefugioRepository extends MongoRepository<NodoRefugioDocument, String> {
    Optional<NodoRefugioDocument> findByNombre(String nombre);
}