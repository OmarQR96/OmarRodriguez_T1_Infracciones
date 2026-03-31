package edu.pe.cibertec.infracciones.dto;

import lombok.Data;

@Data
public class InfractorResponseDTO {
    private Long id;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private boolean bloqueado;
}