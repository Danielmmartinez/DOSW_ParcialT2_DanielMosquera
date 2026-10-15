package edu.dosw.parcial.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import edu.dosw.parcial.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import edu.dosw.parcial.config.security.JwtAuthFilter;
import edu.dosw.parcial.config.security.JwtUtil;
import edu.dosw.parcial.controller.dtos.response.SolicitudAdoptadaResponse;
import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.core.services.SolicitudService;
import edu.dosw.parcial.main.ParcialApplication;

@WebMvcTest(controllers = SolicitudesController.class)
@ContextConfiguration(classes = ParcialApplication.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthFilter.class})
@ActiveProfiles("test")

public class SolicitudesControllerConsultarAdoptadasTest {
    
    private static final String URL = "/api/v1/solicitudes/adoptadas";

    @Autowired private MockMvc mockMvc;

    @MockBean private SolicitudService solicitudService;
    @MockBean private JwtUtil jwtUtil;

    @Test 
    @DisplayName("Sin token: 401")
    void consultarAdoptadas_sinToken_debeRetornar401() throws Exception{
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("ADOPTANTE: 200 con el listado y el servicio recibe su id y rol")
    void consultarAdoptadas_adoptante_debeRetornar200() throws Exception{
        when(solicitudService.consultarAdoptadas("u1", Rol.ADOPTANTE)).thenReturn(List.of(SolicitudAdoptadaResponse.builder().idMascota("PERRO-001").estadoMascota("EN_PROCESO").fechaRecibimiento(LocalDate.of(2026, 10, 20)).tieneNinos(true).tieneOtrasMascotas(false).tieneEspacioReducido(false).build()));

        mockMvc.perform(get(URL)).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].idMascota").value("PERRO-001"))
        .andExpect(jsonPath("$[0].estadoMascota").value("EN_PROCESO"))
        .andExpect(jsonPath("$[0].fechaRecibimiento").value("2026-10-20"))
        .andExpect(jsonPath("$[0].tieneNinos").value(true));

        verify(solicitudService).consultarAdoptadas("u1", Rol.ADOPTANTE);
    }

    @Test
    @WithMockUser(username = "admin-id", roles = "ADMIN")
    @DisplayName("ADMIN: 200 y el servicio recibe el rol ADMIN")
    void consultarAdoptadas_admin_debeRetornar200() throws Exception {
        when(solicitudService.consultarAdoptadas("admin-id", Rol.ADMIN)).thenReturn(List.of());

        mockMvc.perform(get(URL)).andExpect(status().isOk());

        verify(solicitudService).consultarAdoptadas("admin-id", Rol.ADMIN);
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("E11: sin resultados responde 200 con lista vacia")
    void consultarAdoptadas_sinResultados_debeRetornar200ListaVacia() throws Exception{
        when(solicitudService.consultarAdoptadas("u1", Rol.ADOPTANTE)).thenReturn(List.of());

        mockMvc.perform(get(URL)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$").isEmpty());
    }
}
