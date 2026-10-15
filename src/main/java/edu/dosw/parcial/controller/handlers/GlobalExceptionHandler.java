package edu.dosw.parcial.controller.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import edu.dosw.parcial.controller.handlers.exceptions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciales(CredencialesInvalidasException e) {
        log.warn("Login fallido: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorBody("CREDENCIALES_INVALIDAS", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Acceso denegado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorBody("ACCESO_DENEGADO", "No tienes permisos para realizar esta acción"));
    }

    @ExceptionHandler(SolicitudAjenaException.class)
    public ResponseEntity<Map<String, Object>> handleSolicitudAjena(SolicitudAjenaException e){
        log.warn("Acceso a recurso ajeno: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody("ACCESO_DENEGADO", e.getMessage()));
    }

    @ExceptionHandler({NodoDeOrigenInvalidoException.class, MascotaNoEncontradaException.class, SolicitudNoEncontradaException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody("RECURSO_NO_ENCONTRADO", e.getMessage()));
    }

    @ExceptionHandler({MascotaNoDisponibleException.class, SegundaSolicitudException.class, SolicitudNoAprobadaException.class, SolicitudYaAplazadaException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException e) {
        log.warn("Conflicto de negocio: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody("CONFLICTO_ESTADO", e.getMessage()));
    }

    @ExceptionHandler({AdopcionIncompatibleException.class, FechaRecibimientoInvalidaException.class, DatosMascotaInvalidosException.class})
    public ResponseEntity<Map<String, Object>> handleUnprocessableEntity(RuntimeException e) {
        log.warn("Regla de negocio no procesable: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody("REGLA_NEGOCIO_INVALIDA", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> campos = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err ->
                campos.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = errorBody("DATOS_INVALIDOS", "Hay campos inválidos en la petición");
        body.put("errores", campos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception e) {
        log.warn("Peticion malformada: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody("PETICION_INVALIDA", "La peticion esta mal formada o tiene parametros invalidos"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Error inesperado en el servidor", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("ERROR_INTERNO", "Ha ocurrido un error inesperado en el servidor"));
    }

    private Map<String, Object> errorBody(String codigo, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("codigo", codigo);
        body.put("mensaje", mensaje);
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}