package edu.pe.cibertec.infracciones.exception;

public class MultaNotFoundException extends RuntimeException {
    public MultaNotFoundException(Long id) {
        super("Multa no encontrada con id: " + id);
    }
}