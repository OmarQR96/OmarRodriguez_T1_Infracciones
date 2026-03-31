package edu.pe.cibertec.infracciones.dto;

import lombok.Data;

@Data
public class VehiculoResponseDTO {
    private Long id;
    private String placa;
    private String marca;
    private Integer anio;
    private boolean suspendido;
}