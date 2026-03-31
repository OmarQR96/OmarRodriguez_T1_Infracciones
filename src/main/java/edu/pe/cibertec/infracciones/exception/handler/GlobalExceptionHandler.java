package edu.pe.cibertec.infracciones.exception.handler;

import edu.pe.cibertec.infracciones.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InfractorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInfractorNotFound(InfractorNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(VehiculoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVehiculoNotFound(VehiculoNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MultaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMultaNotFound(MultaNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InfractorBloqueadoException.class)
    public ResponseEntity<Map<String, Object>> handleInfractorBloqueado(InfractorBloqueadoException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(VehiculoSuspendidoException.class)
    public ResponseEntity<Map<String, Object>> handleVehiculoSuspendido(VehiculoSuspendidoException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(TipoInfraccionDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInfraccionDuplicada(TipoInfraccionDuplicadaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PagoYaRealizadoException.class)
    public ResponseEntity<Map<String, Object>> handlePagoYaRealizado(PagoYaRealizadoException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}