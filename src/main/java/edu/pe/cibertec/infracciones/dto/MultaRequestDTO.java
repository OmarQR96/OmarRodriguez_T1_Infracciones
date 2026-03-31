package edu.pe.cibertec.infracciones.dto;

import lombok.Data;
import java.util.List;

@Data
public class MultaRequestDTO {
    private Long infractorId;
    private Long vehiculoId;
    private List<Long> tiposInfraccionIds;
}