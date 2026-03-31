package edu.pe.cibertec.infracciones.dto;

import lombok.Data;

@Data
public class VehiculoRequestDTO {
    private String placa;
    private String marca;
    private Integer anio;
}