package edu.dosw.parcial.persistence.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.dosw.parcial.persistence.document.UsuarioDocument;

@Repository 
public interface UsuarioRepository extends MongoRepository<UsuarioDocument, String>{

    Optional<UsuarioDocument> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
