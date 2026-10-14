package edu.dosw.parcial.controller.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.dosw.parcial.controller.handlers.exceptions.CredencialesInvalidasException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciales(CredencialesInvalidasException e) {
        log.warn("Login fallido: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody(
                "CREDENCIALES_INVALIDAS", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Acceso denegado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody(
                "ACCESO_DENEGADO", "No tienes permisos para realizar esta acción"));
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

    private Map<String, Object> errorBody(String codigo, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("codigo", codigo);
        body.put("mensaje", mensaje);
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}