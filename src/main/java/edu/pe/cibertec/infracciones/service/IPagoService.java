package edu.pe.cibertec.infracciones.service;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import java.util.List;

public interface IPagoService {
    PagoResponseDTO procesarPago(Long multaId);
    List<PagoResponseDTO> obtenerPagosPorInfractor(Long infractorId);
}