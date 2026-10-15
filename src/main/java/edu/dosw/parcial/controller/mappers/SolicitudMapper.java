package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.core.models.SolicitudAdopcion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarioId", ignore = true)
    @Mapping(target = "mascotaId", source = "idMascota")
    @Mapping(target = "fechaSolicitud", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "aplazada", ignore = true)
    @Mapping(target = "fechaAplazamiento", ignore = true)
    SolicitudAdopcion toDomain(CrearSolicitudRequest request);

    @Mapping(target = "idSolicitud", source = "id")
    @Mapping(target = "estadoSolicitud", source = "estado")
    CrearSolicitudResponse toResponseDTO(SolicitudAdopcion domain);
}