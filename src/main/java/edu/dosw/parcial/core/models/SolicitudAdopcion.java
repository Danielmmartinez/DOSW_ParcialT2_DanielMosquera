package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAdopcion {
    private String id;
    private String usuarioId;
    private String mascotaId;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaRecibimiento;
    private EstadoSolicitud estado;
    private boolean aplazada;
    private LocalDate fechaAplazamiento;
    private boolean tieneNinos;
    private boolean tieneOtrasMascotas;
    private boolean tieneEspacioReducido;

    public boolean perteneceA(String usuarioId){
        return this.usuarioId != null && this.usuarioId.equals(usuarioId);
    }

    public boolean estaAprobada(){
        return EstadoSolicitud.APROBADA.equals(this.estado);
    }

    public void aplazar(LocalDate nuevaFecha, LocalDate fechaAplazamiento){
        this.fechaRecibimiento = nuevaFecha;
        this.fechaAplazamiento = fechaAplazamiento;
        this.aplazada = true;
    }
}