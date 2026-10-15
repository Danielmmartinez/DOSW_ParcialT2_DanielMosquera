package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.response.LoginResponse;
import edu.dosw.parcial.core.models.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface AuthMapper {
    
    @Mapping(target = "token", source="token")
    @Mapping(target = "tipo", constant = "Bearer")
    @Mapping(target = "rol", source = "usuario.rol", qualifiedByName = "rolToString")
    @Mapping(target = "nombre", source="usuario.nombre")
    LoginResponse toResponseDTO(Usuario usuario, String token);

    @Named("rolToString")
    default String rolToString(edu.dosw.parcial.core.models.Rol rol){
        return rol != null ? rol.name() : null;
    }
} 