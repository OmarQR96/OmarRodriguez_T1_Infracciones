package edu.pe.cibertec.infracciones.dto;

import lombok.Data;

@Data
public class InfractorRequestDTO {
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
}