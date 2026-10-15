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

    private CrearMascotaRequest request;
    private NodoRefugioDocument refugioValido;
    private Mascota mascota;
    private MascotaDocument mascotaDoc;

    @BeforeEach
    void setUp() {
        request = CrearMascotaRequest.builder()
                .nombre("Toby")
                .especie("PERRO")
                .edadMeses(12)
                .tamano("MEDIANO")
                .refugioId("ref-sur")
                .compatibleNinos(true)
                .compatibleOtrasMascotas(true)
                .compatibleEspaciosReducidos(true)
                .build();

        refugioValido = NodoRefugioDocument.builder()
                .id("ref-sur")
                .nombre("Refugio Sur")
                .tipo(TipoNodo.REFUGIO)
                .build();

        mascota = Mascota.builder()
                .id("PERRO-001")
                .nombre("Toby")
                .especie(Especie.PERRO)
                .tamano(Tamano.MEDIANO)
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .build();

        mascotaDoc = MascotaDocument.builder()
                .id("PERRO-001")
                .nombre("Toby")
                .especie(Especie.PERRO)
                .tamano(Tamano.MEDIANO)
                .estadoMascota(EstadoMascota.DISPONIBLE)
                .build();
    }

    @Test
    @DisplayName("Arrange-Act-Assert: Crear mascota exitosamente")
    void crearMascota_exitoso() {
        // Arrange
        when(nodoRefugioRepository.findById("ref-sur")).thenReturn(Optional.of(refugioValido));
        when(mascotaRepository.countByEspecie(Especie.PERRO)).thenReturn(0L);
        when(mascotaMapper.toDomain(request)).thenReturn(mascota);
        when(entityMapper.toDocument(any(Mascota.class))).thenReturn(mascotaDoc);
        when(mascotaRepository.save(mascotaDoc)).thenReturn(mascotaDoc);
        when(entityMapper.toDomain(mascotaDoc)).thenReturn(mascota);
        when(mascotaMapper.toResponseDTO(mascota)).thenReturn(new CrearMascotaResponse("PERRO-001"));

        // Act
        CrearMascotaResponse response = mascotaService.crearMascota(request);

        // Assert
        assertNotNull(response);
        assertEquals("PERRO-001", response.getIdMascota());
        verify(mascotaRepository).save(any());
    }

    @Test
    @DisplayName("E03 - Nodo no existe: Debe lanzar NodoDeOrigenInvalidoException")
    void crearMascota_refugioNoExiste() {
        // Arrange
        when(nodoRefugioRepository.findById("ref-sur")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NodoDeOrigenInvalidoException.class, () -> mascotaService.crearMascota(request));
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E03 - Nodo no es REFUGIO: Debe lanzar NodoDeOrigenInvalidoException")
    void crearMascota_nodoEsCiudad() {
        // Arrange
        NodoRefugioDocument ciudad = NodoRefugioDocument.builder().id("ref-sur").tipo(TipoNodo.CIUDAD).build();
        when(nodoRefugioRepository.findById("ref-sur")).thenReturn(Optional.of(ciudad));

        // Act & Assert
        assertThrows(NodoDeOrigenInvalidoException.class, () -> mascotaService.crearMascota(request));
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E02 - Especie inválida: Debe lanzar DatosMascotaInvalidosException")
    void crearMascota_especieInvalida() {
        // Arrange
        request.setEspecie("DINOSAURIO");

        // Act & Assert
        assertThrows(DatosMascotaInvalidosException.class, () -> mascotaService.crearMascota(request));
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E02 - Tamaño inválido: Debe lanzar DatosMascotaInvalidosException")
    void crearMascota_tamanoInvalido() {
        // Arrange
        request.setTamano("GIGANTESCO");

        // Act & Assert
        assertThrows(DatosMascotaInvalidosException.class, () -> mascotaService.crearMascota(request));
        verify(mascotaRepository, never()).save(any());
    }
}