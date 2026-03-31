package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.VehiculoRequestDTO;
import edu.pe.cibertec.infracciones.dto.VehiculoResponseDTO;
import edu.pe.cibertec.infracciones.exception.VehiculoNotFoundException;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.IVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements IVehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Override
    public VehiculoResponseDTO registrarVehiculo(VehiculoRequestDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(dto.getPlaca());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setAnio(dto.getAnio());
        vehiculo.setSuspendido(false);
        return mapToResponse(vehiculoRepository.save(vehiculo));
    }

    @Override
    public VehiculoResponseDTO obtenerVehiculoPorId(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new VehiculoNotFoundException(id));
        return mapToResponse(vehiculo);
    }

    @Override
    public List<VehiculoResponseDTO> obtenerTodos() {
        return vehiculoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private VehiculoResponseDTO mapToResponse(Vehiculo vehiculo) {
        VehiculoResponseDTO dto = new VehiculoResponseDTO();
        dto.setId(vehiculo.getId());
        dto.setPlaca(vehiculo.getPlaca());
        dto.setMarca(vehiculo.getMarca());
        dto.setAnio(vehiculo.getAnio());
        dto.setSuspendido(vehiculo.isSuspendido());
        return dto;
    }
}