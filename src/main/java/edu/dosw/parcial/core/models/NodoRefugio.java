package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class NodoRefugio {
    private String id;
    private String nombre;
    private TipoNodo tipo;
    private String padreId;

    public boolean esRefugio() {
        return TipoNodo.REFUGIO.equals(this.tipo);
    }
}