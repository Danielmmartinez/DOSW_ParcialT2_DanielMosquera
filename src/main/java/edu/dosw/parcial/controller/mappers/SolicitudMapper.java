package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.AplazarSolicitudResponse;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.controller.dtos.response.SolicitudAdoptadaResponse;
import edu.dosw.parcial.core.models.EstadoMascota;
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

    @Mapping(target = "idMascota", source = "solicitud.mascotaId")
    @Mapping(target = "estadoMascota", source = "estadoMascota")
    @Mapping(target = "fechaRecibimiento", source = "solicitud.fechaRecibimiento")
    @Mapping(target = "tieneNinos", source = "solicitud.tieneNinos")
    @Mapping(target = "tieneOtrasMascotas", source = "solicitud.tieneOtrasMascotas")
    @Mapping(target = "tieneEspacioReducido", source = "solicitud.tieneEspacioReducido")
    SolicitudAdoptadaResponse toAdoptadaResponse(SolicitudAdopcion solicitud, EstadoMascota estadoMascota);

    @Mapping(target = "idSolicitud", source = "id")
    @Mapping(target = "fechaNuevaRecibimiento", source = "fechaRecibimiento")
    AplazarSolicitudResponse toAplazarResponse(SolicitudAdopcion domain);
}