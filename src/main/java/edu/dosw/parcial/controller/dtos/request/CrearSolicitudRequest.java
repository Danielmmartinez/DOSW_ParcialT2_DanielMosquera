package edu.dosw.parcial.controller.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición para adoptar una mascota")
public class CrearSolicitudRequest {

    @NotBlank(message = "El idMascota es obligatorio")
    @Schema(example = "PERRO-001")
    private String idMascota;

    @NotNull(message = "La fecha de recibimiento es obligatoria")
    @Future(message = "La fecha debe ser posterior al día de hoy")
    @Schema(example = "2026-05-20")
    private LocalDate fechaRecibimiento;

    @NotNull(message = "Debe indicar si tiene niños")
    @Schema(example = "true")
    private Boolean tieneNinos;

    @NotNull(message = "Debe indicar si tiene otras mascotas")
    @Schema(example = "false")
    private Boolean tieneOtrasMascotas;

    @NotNull(message = "Debe indicar si tiene espacio reducido")
    @Schema(example = "false")
    private Boolean tieneEspacioReducido;
}