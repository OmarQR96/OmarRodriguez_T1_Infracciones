package edu.pe.cibertec.infracciones.exception;

public class VehiculoSuspendidoException extends RuntimeException {
    public VehiculoSuspendidoException(Long id) {
        super("El vehículo con id: " + id + " se encuentra suspendido");
    }
}