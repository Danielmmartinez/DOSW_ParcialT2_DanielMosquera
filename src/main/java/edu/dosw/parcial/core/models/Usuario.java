package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class Usuario {
    
    private String id;
    private String correo;
    private String password;
    private String nombre;
    private Rol rol;

    public boolean esAdmin(){
        return Rol.ADMIN.equals(this.rol);
    }
}
