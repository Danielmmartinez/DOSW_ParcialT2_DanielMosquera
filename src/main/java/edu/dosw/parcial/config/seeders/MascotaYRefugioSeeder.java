package edu.dosw.parcial.config.seeders;

import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Tamano;
import edu.dosw.parcial.core.models.TipoNodo;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import edu.dosw.parcial.persistence.document.NodoRefugioDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.NodoRefugioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class MascotaYRefugioSeeder implements CommandLineRunner {

    private final NodoRefugioRepository nodoRefugioRepository;
    private final MascotaRepository mascotaRepository;

    @Override
    public void run(String... args) {
        if (nodoRefugioRepository.count() > 0) {
            log.info("Datos de refugios ya inicializados");
            return;
        }

        NodoRefugioDocument red = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("red-nacional").nombre("Red Nacional ECI Paw Connect").tipo(TipoNodo.RED).build());

        NodoRefugioDocument bogota = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("ciudad-bogota").nombre("Bogotá").tipo(TipoNodo.CIUDAD).padreId(red.getId()).build());
        NodoRefugioDocument medellin = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("ciudad-medellin").nombre("Medellín").tipo(TipoNodo.CIUDAD).padreId(red.getId()).build());
        NodoRefugioDocument cali = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("ciudad-cali").nombre("Cali").tipo(TipoNodo.CIUDAD).padreId(red.getId()).build());

        NodoRefugioDocument refNorte = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("refugio-norte").nombre("Refugio Norte").tipo(TipoNodo.REFUGIO).padreId(bogota.getId()).build());
        NodoRefugioDocument refSur = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("refugio-sur").nombre("Refugio Sur").tipo(TipoNodo.REFUGIO).padreId(bogota.getId()).build());
        NodoRefugioDocument refCentral = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("refugio-central").nombre("Refugio Central").tipo(TipoNodo.REFUGIO).padreId(medellin.getId()).build());
        NodoRefugioDocument refOeste = nodoRefugioRepository.save(NodoRefugioDocument.builder()
                .id("refugio-oeste").nombre("Refugio Oeste").tipo(TipoNodo.REFUGIO).padreId(cali.getId()).build());

        crearMascota("PERRO-001", "Max", Especie.PERRO, 24, Tamano.MEDIANO, true, true, true, refNorte.getId());
        crearMascota("PERRO-002", "Bruno", Especie.PERRO, 72, Tamano.GRANDE, true, false, false, refNorte.getId());
        crearMascota("GATO-001", "Luna", Especie.GATO, 18, Tamano.PEQUEÑO, true, true, true, refNorte.getId());
        crearMascota("PERRO-003", "Toby", Especie.PERRO, 12, Tamano.PEQUEÑO, true, true, true, refSur.getId());
        crearMascota("GATO-002", "Milo", Especie.GATO, 48, Tamano.MEDIANO, false, true, true, refSur.getId());
        crearMascota("CONEJO-001", "Copito", Especie.CONEJO, 10, Tamano.PEQUEÑO, true, true, true, refSur.getId());
        crearMascota("PERRO-004", "Rocky", Especie.PERRO, 60, Tamano.GRANDE, false, false, false, refCentral.getId());
        crearMascota("GATO-003", "Nala", Especie.GATO, 30, Tamano.PEQUEÑO, true, true, true, refCentral.getId());
        crearMascota("AVE-001", "Kiwi", Especie.AVE, 20, Tamano.PEQUEÑO, true, false, true, refCentral.getId());
        crearMascota("PERRO-005", "Simon", Especie.PERRO, 36, Tamano.MEDIANO, true, true, true, refOeste.getId());
        crearMascota("REPTIL-001", "Rocco", Especie.REPTIL, 84, Tamano.PEQUEÑO, false, false, true, refOeste.getId());
        crearMascota("GATO-004", "Zoe", Especie.GATO, 8, Tamano.PEQUEÑO, true, true, true, refOeste.getId());

        log.info("Datos iniciales de Refugios y Mascotas cargados con éxito");
    }

    private void crearMascota(String id, String nom, Especie esp, int edad, Tamano tam,
                              boolean ninos, boolean mascs, boolean espac, String refId) {
        mascotaRepository.save(MascotaDocument.builder()
                .id(id).nombre(nom).especie(esp).edadMeses(edad).tamano(tam)
                .compatibleNinos(ninos).compatibleMascotas(mascs).compatibleEspaciosReducidos(espac)
                .refugioId(refId).estadoMascota(EstadoMascota.DISPONIBLE).build());
    }
}