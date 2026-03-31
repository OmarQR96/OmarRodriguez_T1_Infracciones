package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.MultaRequestDTO;
import edu.pe.cibertec.infracciones.dto.MultaResponseDTO;
import edu.pe.cibertec.infracciones.exception.*;
import edu.pe.cibertec.infracciones.model.*;
import edu.pe.cibertec.infracciones.repository.*;
import edu.pe.cibertec.infracciones.service.IMultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultaServiceImpl implements IMultaService {

    private final MultaRepository multaRepository;
    private final InfractorRepository infractorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final TipoInfraccionRepository tipoInfraccionRepository;

    @Override
    public MultaResponseDTO registrarMulta(MultaRequestDTO dto) {
        Infractor infractor = infractorRepository.findById(dto.getInfractorId())
                .orElseThrow(() -> new InfractorNotFoundException(dto.getInfractorId()));

        if (infractor.isBloqueado())
            throw new InfractorBloqueadoException(dto.getInfractorId());

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new VehiculoNotFoundException(dto.getVehiculoId()));

        if (vehiculo.isSuspendido())
            throw new VehiculoSuspendidoException(dto.getVehiculoId());

        List<TipoInfraccion> tipos = new ArrayList<>();
        List<String> codigosVistos = new ArrayList<>();

        for (Long tipoId : dto.getTiposInfraccionIds()) {
            TipoInfraccion tipo = tipoInfraccionRepository.findById(tipoId)
                    .orElseThrow(() -> new RuntimeException("Tipo de infracción no encontrado: " + tipoId));
            if (codigosVistos.contains(tipo.getCodigo()))
                throw new TipoInfraccionDuplicadaException(tipo.getCodigo());
            codigosVistos.add(tipo.getCodigo());
            tipos.add(tipo);
        }

        double monto = tipos.stream().mapToDouble(TipoInfraccion::getMontoBase).sum();

        Multa multa = new Multa();
        multa.setCodigo("MUL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        multa.setMonto(monto);
        multa.setFechaEmision(LocalDate.now());
        multa.setFechaVencimiento(LocalDate.now().plusDays(30));
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setInfractor(infractor);
        multa.setVehiculo(vehiculo);
        multa.setTiposInfraccion(tipos);

        Multa saved = multaRepository.save(multa);

        long multasPendientesVehiculo = multaRepository
                .findByVehiculo_IdAndEstado(vehiculo.getId(), EstadoMulta.PENDIENTE).size();
        if (multasPendientesVehiculo >= 3) {
            vehiculo.setSuspendido(true);
            vehiculoRepository.save(vehiculo);
        }

        return mapToResponse(saved);
    }

    @Override
    public MultaResponseDTO obtenerMultaPorId(Long id) {
        Multa multa = multaRepository.findById(id)
                .orElseThrow(() -> new MultaNotFoundException(id));
        return mapToResponse(multa);
    }

    @Override
    public List<MultaResponseDTO> obtenerMultasPorInfractor(Long infractorId) {
        return multaRepository.findByInfractor_Id(infractorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<MultaResponseDTO> obtenerMultasPorVehiculo(Long vehiculoId) {
        return multaRepository.findByVehiculo_Id(vehiculoId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MultaResponseDTO mapToResponse(Multa multa) {
        MultaResponseDTO dto = new MultaResponseDTO();
        dto.setId(multa.getId());
        dto.setCodigo(multa.getCodigo());
        dto.setMonto(multa.getMonto());
        dto.setFechaEmision(multa.getFechaEmision());
        dto.setFechaVencimiento(multa.getFechaVencimiento());
        dto.setEstado(multa.getEstado());
        dto.setInfractorNombre(multa.getInfractor().getNombre() + " " + multa.getInfractor().getApellido());
        dto.setVehiculoPlaca(multa.getVehiculo().getPlaca());
        dto.setTiposInfraccion(multa.getTiposInfraccion().stream()
                .map(TipoInfraccion::getDescripcion)
                .toList());
        return dto;
    }
}