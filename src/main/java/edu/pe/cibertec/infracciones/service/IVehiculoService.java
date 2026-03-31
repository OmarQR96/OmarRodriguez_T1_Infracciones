package edu.pe.cibertec.infracciones.service;

import edu.pe.cibertec.infracciones.dto.VehiculoRequestDTO;
import edu.pe.cibertec.infracciones.dto.VehiculoResponseDTO;
import java.util.List;

public interface IVehiculoService {
    VehiculoResponseDTO registrarVehiculo(VehiculoRequestDTO dto);
    VehiculoResponseDTO obtenerVehiculoPorId(Long id);
    List<VehiculoResponseDTO> obtenerTodos();
}