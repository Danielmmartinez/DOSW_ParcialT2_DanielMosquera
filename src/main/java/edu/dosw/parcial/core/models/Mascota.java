package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data 
@Builder 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Mascota {
    
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

    public boolean estaDisponible(){return estadoMascota==EstadoMascota.DISPONIBLE;}
    public void marcarEnProceso(){estadoMascota = EstadoMascota.EN_PROCESO;}
    public boolean esCompatible(boolean ninos, boolean mascotas, boolean espacio){
        return compatibleNinos==ninos && 
                compatibleEspaciosReducidos==mascotas && 
                compatibleEspaciosReducidos == espacio;
    }

}
