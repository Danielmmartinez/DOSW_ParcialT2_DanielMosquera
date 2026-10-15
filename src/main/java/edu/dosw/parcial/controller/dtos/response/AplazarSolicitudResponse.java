package edu.dosw.parcial.controller.dtos.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Schema(description = "Respuesta de un aplazamiento exitoso")
public class AplazarSolicitudResponse {
    
    @Schema(example = "SOL-002")
    private String idSolicitud;

    @Schema(example = "2026-10-16")
    private LocalDate fechaNuevaRecibimiento;
}
