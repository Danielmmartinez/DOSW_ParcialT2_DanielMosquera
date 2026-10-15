package edu.dosw.parcial.persistence.document;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "solicitudes")
public class SolicitudAdopcionDocument {
    @Id
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
}