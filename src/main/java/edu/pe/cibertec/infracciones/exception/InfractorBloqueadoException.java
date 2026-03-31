package edu.pe.cibertec.infracciones.exception;

public class InfractorBloqueadoException extends RuntimeException {
    public InfractorBloqueadoException(Long id) {
        super("El infractor con id: " + id + " se encuentra bloqueado");
    }
}