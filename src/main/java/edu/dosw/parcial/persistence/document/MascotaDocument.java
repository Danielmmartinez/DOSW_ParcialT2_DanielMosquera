package edu.dosw.parcial.persistence.document;

import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Tamano;
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
@Document(collection = "mascotas")
public class MascotaDocument {
    @Id
    private String id;
    private String nombre;
    private Especie especie;
    private int edadMeses;
    private Tamano tamano;
    private String refugioId;
    private boolean compatibleNinos;
    private boolean compatibleMascotas;
    private boolean compatibleEspaciosReducidos;
    private EstadoMascota estadoMascota;
}