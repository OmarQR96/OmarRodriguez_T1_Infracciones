package edu.pe.cibertec.infracciones.exception;

public class TipoInfraccionDuplicadaException extends RuntimeException {
    public TipoInfraccionDuplicadaException(String codigo) {
        super("El tipo de infracción ya fue registrado en esta multa: " + codigo);
    }
}