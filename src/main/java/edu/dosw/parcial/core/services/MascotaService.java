package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequest;
import edu.dosw.parcial.controller.dtos.response.CrearMascotaResponse;

public interface MascotaService {
    CrearMascotaResponse crearMascota(CrearMascotaRequest request);
}