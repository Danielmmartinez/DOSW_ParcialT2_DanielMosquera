package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.CrearSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.CrearSolicitudResponse;
import edu.dosw.parcial.controller.dtos.response.SolicitudAdoptadaResponse;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.services.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Adopción", description = "Gestión de solicitudes de adopción")
@SecurityRequirement(name = "bearerAuth")
public class SolicitudesController {

    private final SolicitudService solicitudService;

    @PostMapping
    @PreAuthorize("hasRole('ADOPTANTE')")
    @Operation(summary = "Adoptar una mascota", description = "Crea una solicitud de adopción en estado APROBADA (Solo ADOPTANTE)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud de adopción creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Petición malformada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (requiere rol ADOPTANTE)"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada"),
            @ApiResponse(responseCode = "409", description = "Mascota no disponible (E06) o adoptante con solicitud activa (E8)"),
            @ApiResponse(responseCode = "422", description = "Incompatibilidad con la mascota (E04) o fecha fuera de rango (E07)")
    })
    public ResponseEntity<CrearSolicitudResponse> crearSolicitud(
            Authentication authentication,
            @Valid @RequestBody CrearSolicitudRequest request) {

        String usuarioId = authentication.getName();
        CrearSolicitudResponse response = solicitudService.adoptar(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/adoptadas")
    @PreAuthorize("hasAnyRole('ADOPTANTE','ADMIN')")
    @Operation(summary = "Consultar mascotas adoptadas", description = "Lista las solicitudes APROBADAS en orden descendente por fecha de recibimiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado (puede ser vacio)"),
        @ApiResponse(responseCode = "400", description = "Parametros Invalidos"),
        @ApiResponse(responseCode = "401", description = "Sin token de autenticacion")
    })
    public ResponseEntity<List<SolicitudAdoptadaResponse>> consultarAdoptadas(Authentication authentication){
        Rol rol = esAdmin(authentication) ? Rol.ADMIN : Rol.ADOPTANTE;
        return ResponseEntity.ok(solicitudService.consultarAdoptadas(authentication.getName(), rol));
    }

    private boolean esAdmin(Authentication authenticacion){
        return authenticacion.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}