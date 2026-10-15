package edu.dosw.parcial.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.persistence.document.UsuarioDocument;

@Mapper(componentModel = "spring")
public interface UsuarioEntityMapper {
    
    @Mapping(target = "rol", source = "rol", qualifiedByName = "rolToString")
    UsuarioDocument toDocument(Usuario usuario);

    @Mapping(target = "rol", source = "rol", qualifiedByName = "stringToRol")
    Usuario toDomain(UsuarioDocument document);

    @Named("rolToString")
    default String rolToString(Rol rol){
        return rol != null ? rol.name() : null;
    }

    @Named("stringToRol")
    default Rol stringToRol(String rol){
        return rol != null ? Rol.valueOf(rol) : null;
    }
}
