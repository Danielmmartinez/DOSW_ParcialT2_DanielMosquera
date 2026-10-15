package edu.dosw.parcial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import edu.dosw.parcial.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import edu.dosw.parcial.config.security.JwtAuthFilter;
import edu.dosw.parcial.config.security.JwtUtil;
import edu.dosw.parcial.controller.dtos.request.AplazarSolicitudRequest;
import edu.dosw.parcial.controller.dtos.response.AplazarSolicitudResponse;
import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.controller.handlers.exceptions.FechaRecibimientoInvalidaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudAjenaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudNoEncontradaException;
import edu.dosw.parcial.controller.handlers.exceptions.SolicitudYaAplazadaException;
import edu.dosw.parcial.core.services.SolicitudService;
import edu.dosw.parcial.main.ParcialApplication;

@WebMvcTest(controllers = SolicitudesController.class)
@ContextConfiguration(classes = ParcialApplication.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthFilter.class})
@ActiveProfiles("test")
public class SolicitudesControllerAplazarTest {
    private static final String URL = "/api/v1/solicitudes/SOL-002/aplazar";
    private static final String BODY_VALIDO = "{\"nuevaFechaRecibimiento\":\"2026-10-16\"}";

    @Autowired private MockMvc mockMvc;

    @MockBean private SolicitudService solicitudService;
    @MockBean private JwtUtil jwtUtil;

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("200: aplaza y responde idSolicitud y fechaNuevaRecibimiento")
    void aplazar_exitoso_debeRetornar200() throws Exception{
        when(solicitudService.aplazar(eq("u1"), eq("SOL-002"), any(AplazarSolicitudRequest.class))).thenReturn(AplazarSolicitudResponse.builder().idSolicitud("SOL-002").fechaNuevaRecibimiento(LocalDate.of(2026,10,16)).build());

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isOk()).andExpect(jsonPath("$.idSolicitud").value("SOL-002")).andExpect(jsonPath("$.fechaNuevaRecibimiento").value("2026-10-16"));
    }

    @Test 
    @DisplayName("401: sin token")
    void aplazar_sinToken_debeRetornar401() throws Exception{
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isUnauthorized());
    }

    @Test 
    @WithMockUser(username = "admin-id", roles = "ADMIN")
    @DisplayName("403: rol ADMIN no puede aplazar y el servicio no se invoca")
    void aplazar_rolAdmin_debeRetornar403() throws Exception{
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isForbidden());

        verify(solicitudService, never()).aplazar(any(), any(), any());
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("400: falta la nueva fecha")
    void aplazar_sinFecha_debeRetornar400() throws Exception {
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("400: JSON malformado")
    void aplazar_jsonMalformado_debeRetornar400() throws Exception {
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content("{\"nuevaFechaRecibimiento\":")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("PETICION_INVALIDA"));
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("404: la solicitud no existe")
    void aplazar_solicitudInexistente_debeRetornar404() throws Exception{
        when(solicitudService.aplazar(eq("u1"), eq("SOL-002"), any())).thenThrow(new SolicitudNoEncontradaException("La solicitud no existe"));

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isNotFound()).andExpect(jsonPath("$.codigo").value("RECURSO_NO_ENCONTRADO"));
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("403: solicitud de otro adoptante")
    void aplazar_solicitudAjena_debeRetornar403() throws Exception{
        when(solicitudService.aplazar(eq("u1"), eq("SOL-002"), any())).thenThrow(new SolicitudAjenaException("No puedes aplazar una solicitud que no es tuya"));

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isForbidden()).andExpect(jsonPath("$.codigo").value("ACCESO_DENEGADO"));
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("E9 - 409: solicitud ya aplazada")
    void aplazar_yaAplazada_debeRetornar409() throws Exception {
        when(solicitudService.aplazar(eq("u1"), eq("SOL-002"), any())).thenThrow(new SolicitudYaAplazadaException("La solicitud ya fue aplazada una vez"));

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isConflict()).andExpect(jsonPath("$.codigo").value("CONFLICTO_ESTADO"));
    }

    @Test 
    @WithMockUser(username = "u1", roles = "ADOPTANTE")
    @DisplayName("422: fecha no valida")
    void aplazar_fechaInvalida_debeRetornar422() throws Exception{
        when(solicitudService.aplazar(eq("u1"), eq("SOL-002"), any())).thenThrow(new FechaRecibimientoInvalidaException("Fecha no valida"));

        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO)).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.codigo").value("REGLA_NEGOCIO_INVALIDA"));
    }
}
