package edu.dosw.parcial.persistence.document;

import edu.dosw.parcial.core.models.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Document(collection = "usuarios")
public class UsuarioDocument {
    
    @Id 
    private String id;

    @Indexed(unique=true)
    private String correo;

    private String password;
    private String nombre;
    private Rol rol;
}
