package edu.dosw.parcial.persistence.document;

import edu.dosw.parcial.core.models.TipoNodo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "nodos_refugio")
public class NodoRefugioDocument {
    @Id
    private String id;
    private String nombre;
    private TipoNodo tipo;
    private String padreId;
    private String direccion;
    private String telefono;
}