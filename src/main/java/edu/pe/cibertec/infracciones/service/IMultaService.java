package edu.pe.cibertec.infracciones.service;

import edu.pe.cibertec.infracciones.dto.MultaRequestDTO;
import edu.pe.cibertec.infracciones.dto.MultaResponseDTO;
import java.util.List;

public interface IMultaService {
    MultaResponseDTO registrarMulta(MultaRequestDTO dto);
    MultaResponseDTO obtenerMultaPorId(Long id);
    List<MultaResponseDTO> obtenerMultasPorInfractor(Long infractorId);
    List<MultaResponseDTO> obtenerMultasPorVehiculo(Long vehiculoId);

}