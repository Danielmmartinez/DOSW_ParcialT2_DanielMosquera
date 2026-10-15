package edu.dosw.parcial.controller.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Solicitud de inicio de sesion")
public class LoginRequest {
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene formato valido")
    @Schema(description = "Correo registrado del usuario", example="juan@mail.com")
    private String correo;

    @NotBlank(message="La contraseña es obligatoria")
    @Schema(description = "Contrasena del usuario", example = "contrasena1234")
    private String password;
}
