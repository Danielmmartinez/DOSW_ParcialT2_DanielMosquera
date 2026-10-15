package edu.dosw.parcial.controller.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición para registrar una nueva mascota en el sistema")
public class CrearMascotaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(example = "Max")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Schema(example = "PERRO", allowableValues = {"PERRO", "GATO", "CONEJO", "AVE", "REPTIL"})
    private String especie;

    @NotNull(message = "La edad en meses es obligatoria")
    @Min(value = 0, message = "La edad mínima es 0 meses")
    @Max(value = 360, message = "La edad máxima permitida es 360 meses")
    @Schema(example = "24")
    private Integer edadMeses;

    @NotBlank(message = "El tamaño es obligatorio")
    @Schema(example = "MEDIANO", allowableValues = {"PEQUEÑO", "MEDIANO", "GRANDE"})
    private String tamano;

    @NotBlank(message = "El refugioId es obligatorio")
    @Schema(example = "refugio-norte-bogota")
    private String refugioId;

    @NotNull(message = "La compatibilidad con niños es obligatoria")
    @Schema(example = "true")
    private Boolean compatibleNinos;

    @NotNull(message = "La compatibilidad con otras mascotas es obligatoria")
    @Schema(example = "true")
    private Boolean compatibleOtrasMascotas;

    @NotNull(message = "La compatibilidad con espacios reducidos es obligatoria")
    @Schema(example = "true")
    private Boolean compatibleEspaciosReducidos;
}