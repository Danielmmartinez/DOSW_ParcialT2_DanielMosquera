package edu.dosw.parcial.controller.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
@Schema(description = "Respuesta exitosa del inicio de sesion")
public class LoginResponse {
    
    @Schema(description = "Token JWT para autenticacion")
    private String token;
    @Schema(description = "Tipo de token", example="Bearer")
    private String tipo;
    @Schema(description = "Rol de usuario")
    private String rol;
    @Schema(description = "Nombre del usuario")
    private String nombre;

}
