package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequest;
import edu.dosw.parcial.controller.dtos.response.CrearMascotaResponse;
import edu.dosw.parcial.core.services.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "Gestión del catálogo de mascotas")
@SecurityRequirement(name = "bearerAuth")
public class MascotasController {

    private final MascotaService mascotaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva mascota", description = "Registra una mascota con estado inicial DISPONIBLE (Solo ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mascota creada con éxito"),
            @ApiResponse(responseCode = "400", description = "JSON malformado o campos con formato incorrecto"),
            @ApiResponse(responseCode = "401", description = "Sin token de autenticación"),
            @ApiResponse(responseCode = "403", description = "Rol no autorizado (requiere ADMIN)"),
            @ApiResponse(responseCode = "404", description = "El refugio no existe o no es de tipo REFUGIO (E03)"),
            @ApiResponse(responseCode = "422", description = "Datos de negocio inválidos (E02)")
    })
    public ResponseEntity<CrearMascotaResponse> crearMascota(@Valid @RequestBody CrearMascotaRequest request) {
        CrearMascotaResponse response = mascotaService.crearMascota(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}