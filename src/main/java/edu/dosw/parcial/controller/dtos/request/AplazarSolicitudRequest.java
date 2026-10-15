package edu.dosw.parcial.controller.dtos.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Schema(description = "Peticion para aplazar el recibimiento de una mascota")
public class AplazarSolicitudRequest {
    
    @NotNull(message = "La nueva fecha de recibimiento es obligatoria")
    @Schema(example = "2026-10-16")
    private LocalDate nuevaFechaRecibimiento;
}
