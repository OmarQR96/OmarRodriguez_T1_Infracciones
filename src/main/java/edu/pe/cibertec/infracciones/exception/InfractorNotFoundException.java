package edu.pe.cibertec.infracciones.exception;

public class InfractorNotFoundException extends RuntimeException {
    public InfractorNotFoundException(Long id) {
        super("Infractor no encontrado con id: " + id);
    }
}