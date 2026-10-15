package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MascotaEntityMapper {
    MascotaDocument toDocument(Mascota domain);
    Mascota toDomain(MascotaDocument document);
}