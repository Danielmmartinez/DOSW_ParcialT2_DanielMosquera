package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compatibilidad {
    private boolean compatibleNinos;
    private boolean compatibleMascotas;
    private boolean compatibleEspaciosReducidos;
}