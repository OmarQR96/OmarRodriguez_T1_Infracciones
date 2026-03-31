package edu.pe.cibertec.infracciones.dto;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MultaResponseDTO {
    private Long id;
    private String codigo;
    private Double monto;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private EstadoMulta estado;
    private String infractorNombre;
    private String vehiculoPlaca;
    private List<String> tiposInfraccion;
}