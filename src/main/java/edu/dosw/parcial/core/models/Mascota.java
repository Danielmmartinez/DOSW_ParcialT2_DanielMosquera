package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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

    public boolean estaDisponible() {
        return EstadoMascota.DISPONIBLE.equals(this.estadoMascota);
    }

    public void marcarEnProceso() {
        this.estadoMascota = EstadoMascota.EN_PROCESO;
    }

    public void marcarAdoptada() {
        this.estadoMascota = EstadoMascota.ADOPTADA;
    }
}