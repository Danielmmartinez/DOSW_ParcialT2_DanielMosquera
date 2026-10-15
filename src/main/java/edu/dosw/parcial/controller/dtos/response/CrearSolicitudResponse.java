package edu.dosw.parcial.controller.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de solicitud de adopción creada")
public class CrearSolicitudResponse {
    @Schema(example = "SOL-001")
    private String idSolicitud;
    @Schema(example = "APROBADA")
    private String estadoSolicitud;
}