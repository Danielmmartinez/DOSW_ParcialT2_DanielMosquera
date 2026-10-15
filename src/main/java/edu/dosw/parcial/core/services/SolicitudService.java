package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;

public interface SolicitudService {
    CrearSolicitudResponse adoptar(String usuarioId, CrearSolicitudRequest request);
}