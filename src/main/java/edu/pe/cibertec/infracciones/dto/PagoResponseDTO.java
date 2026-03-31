package edu.pe.cibertec.infracciones.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PagoResponseDTO {
    private Long id;
    private Double montoPagado;
    private LocalDate fechaPago;
    private Double descuentoAplicado;
    private Double recargo;
    private Long multaId;
}