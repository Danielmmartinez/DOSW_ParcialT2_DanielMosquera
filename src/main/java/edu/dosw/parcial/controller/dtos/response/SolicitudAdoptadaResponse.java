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
@Schema (description= "Mascota asignada a una solicitud de adopcion aprobada")

public class SolicitudAdoptadaResponse {
    
    @Schema(example = "PERRO-001")
    private String idMascota;

    @Schema(example = "EN_PROCESO", description = "Estado actual de la mascota")
    private String estadoMascota;

    @Schema(example = "2026-05-20")
    private LocalDate fechaRecibimiento;

    @Schema(example = "true")
    private Boolean tieneNinos;

    @Schema(example = "true")
    private Boolean tieneOtrasMascotas;

    @Schema(example = "true")
    private Boolean tieneEspacioReducido;
}
