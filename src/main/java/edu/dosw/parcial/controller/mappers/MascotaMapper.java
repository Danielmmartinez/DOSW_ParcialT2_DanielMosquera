package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequest;
import edu.dosw.parcial.controller.dtos.response.CrearMascotaResponse;
import edu.dosw.parcial.core.models.Mascota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MascotaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoMascota", ignore = true)
    @Mapping(target = "compatibleMascotas", source = "compatibleOtrasMascotas")
    Mascota toDomain(CrearMascotaRequest request);

    @Mapping(target = "idMascota", source = "id")
    CrearMascotaResponse toResponseDTO(Mascota mascota);
}