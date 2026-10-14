package edu.dosw.parcial.controller.dtos.request;

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
public class LoginRequest {
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene formato valido")
    private String correo;

    @NotBlank(message="La contraseña es obligatoria")
    private String password;
}
