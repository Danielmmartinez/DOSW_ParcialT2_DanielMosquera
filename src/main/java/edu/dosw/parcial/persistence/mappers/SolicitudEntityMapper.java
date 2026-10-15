package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.SolicitudAdopcion;
import edu.dosw.parcial.persistence.document.SolicitudAdopcionDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudEntityMapper {
    SolicitudAdopcionDocument toDocument(SolicitudAdopcion domain);
    SolicitudAdopcion toDomain(SolicitudAdopcionDocument document);
}