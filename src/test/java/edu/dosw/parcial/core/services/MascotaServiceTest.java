package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequest;
import edu.dosw.parcial.controller.dtos.response.CrearMascotaResponse;
import edu.dosw.parcial.controller.handlers.exceptions.DatosMascotaInvalidosException;
import edu.dosw.parcial.controller.handlers.exceptions.NodoDeOrigenInvalidoException;
import edu.dosw.parcial.controller.mappers.MascotaMapper;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.Tamano;
import edu.dosw.parcial.core.models.TipoNodo;
import edu.dosw.parcial.core.services.Impl.MascotaServiceImpl;
import edu.dosw.parcial.persistence.document.MascotaDocument;
import edu.dosw.parcial.persistence.document.NodoRefugioDocument;
import edu.dosw.parcial.persistence.mappers.MascotaEntityMapper;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.NodoRefugioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock private MascotaRepository mascotaRepository;
    @Mock private NodoRefugioRepository nodoRefugioRepository;
    @Mock private MascotaEntityMapper entityMapper;
    @Mock private MascotaMapper mascotaMapper;

    @InjectMocks private MascotaServiceImpl mascotaService;

    private CrearMascotaRequest validRequest;
    private NodoRefugioDocument refugioValido;
    private Mascota mascotaDomain;
    private MascotaDocument mascotaDoc;

    @BeforeEach
    void setUp() {
        validRequest = CrearMascotaRequest.builder()
                .nombre("Max")
                .especie("PERRO")
                .edadMeses(24)
                .tamano("MEDIANO")
                .refugioId("refugio-1")
                .compatibleNinos(true)
                .compatibleOtrasMascotas(true)
                .compatibleEspaciosReducidos(true)
                .build();

        refugioValido = NodoRefugioDocument.builder()
                .id("refugio-1")
                .nombre("Refugio Norte")
                .tipo(TipoNodo.REFUGIO)
                .build();

        mascotaDomain = Mascota.builder()
                .id("PERRO-001")
                .nombre("Max")
                .especie(Especie.PERRO)
                .edadMeses(24)
                .tamano(Tamano.MEDIANO)
                .refugioId("refugio-1")
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .build();

        mascotaDoc = MascotaDocument.builder()
                .id("PERRO-001")
                .nombre("Max")
                .especie(Especie.PERRO)
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .build();
    }

    @Test
    @DisplayName("Debe crear una mascota exitosamente cuando todos los datos son válidos y el refugio existe")
    void crearMascota_exitoso() {
        // Arrange
        when(nodoRefugioRepository.findById("refugio-1")).thenReturn(Optional.of(refugioValido));
        when(mascotaRepository.countByEspecie(Especie.PERRO)).thenReturn(0L);
        when(mascotaMapper.toDomain(validRequest)).thenReturn(mascotaDomain);
        when(entityMapper.toDocument(any(Mascota.class))).thenReturn(mascotaDoc);
        when(mascotaRepository.save(mascotaDoc)).thenReturn(mascotaDoc);
        when(entityMapper.toDomain(mascotaDoc)).thenReturn(mascotaDomain);
        when(mascotaMapper.toResponseDTO(mascotaDomain)).thenReturn(new CrearMascotaResponse("PERRO-001"));

        // Act
        CrearMascotaResponse response = mascotaService.crearMascota(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("PERRO-001", response.getIdMascota());
        verify(mascotaRepository).save(any());
    }

    @Test
    @DisplayName("E03 - Debe lanzar excepción cuando el nodo de origen no existe")
    void crearMascota_refugioNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(nodoRefugioRepository.findById("refugio-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NodoDeOrigenInvalidoException.class, () -> mascotaService.crearMascota(validRequest));
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E03 - Debe lanzar excepción cuando el nodo no es de tipo REFUGIO (e.g. es CIUDAD)")
    void crearMascota_nodoNoEsRefugio_debeLanzarExcepcion() {
        // Arrange
        NodoRefugioDocument nodoCiudad = NodoRefugioDocument.builder()
                .id("ciudad-bogota")
                .nombre("Bogotá")
                .tipo(TipoNodo.CIUDAD)
                .build();
        when(nodoRefugioRepository.findById("refugio-1")).thenReturn(Optional.of(nodoCiudad));

        // Act & Assert
        assertThrows(NodoDeOrigenInvalidoException.class, () -> mascotaService.crearMascota(validRequest));
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E02 - Debe lanzar excepción cuando la especie o el tamaño son inválidos")
    void crearMascota_especieInvalida_debeLanzarExcepcion() {
        // Arrange
        validRequest.setEspecie("DRAGON");

        // Act & Assert
        assertThrows(DatosMascotaInvalidosException.class, () -> mascotaService.crearMascota(validRequest));
        verify(mascotaRepository, never()).save(any());
    }
}