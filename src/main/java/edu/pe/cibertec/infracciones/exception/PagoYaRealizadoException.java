package edu.pe.cibertec.infracciones.exception;

public class PagoYaRealizadoException extends RuntimeException {
    public PagoYaRealizadoException(Long multaId) {
        super("La multa con id: " + multaId + " ya fue pagada");
    }
}