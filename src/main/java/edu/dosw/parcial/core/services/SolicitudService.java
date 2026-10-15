package edu.dosw.parcial.core.services;

import java.util.List;

import edu.dosw.parcial.controller.dtos.request.AplazarSolicitudRequest;
import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.AplazarSolicitudResponse;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.controller.dtos.response.SolicitudAdoptadaResponse;
import edu.dosw.parcial.core.models.Rol;

public interface SolicitudService {
    CrearSolicitudResponse adoptar(String usuarioId, CrearSolicitudRequest request);

    List<SolicitudAdoptadaResponse> consultarAdoptadas(String usuarioId, Rol rol);

    AplazarSolicitudResponse aplazar(String usuarioId, String idSolicitud, AplazarSolicitudRequest request);
}