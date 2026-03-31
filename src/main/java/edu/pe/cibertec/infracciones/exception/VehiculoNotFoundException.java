package edu.pe.cibertec.infracciones.exception;

public class VehiculoNotFoundException extends RuntimeException {
    public VehiculoNotFoundException(Long id) {
        super("Vehículo no encontrado con id: " + id);
    }
}