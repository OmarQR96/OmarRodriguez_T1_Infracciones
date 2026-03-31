package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.InfractorRequestDTO;
import edu.pe.cibertec.infracciones.dto.InfractorResponseDTO;
import edu.pe.cibertec.infracciones.exception.InfractorNotFoundException;
import edu.pe.cibertec.infracciones.exception.VehiculoNotFoundException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.IInfractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfractorServiceImpl implements IInfractorService {

    private final InfractorRepository infractorRepository;
    private final MultaRepository multaRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    public InfractorResponseDTO registrarInfractor(InfractorRequestDTO dto) {
        Infractor infractor = new Infractor();
        infractor.setDni(dto.getDni());
        infractor.setNombre(dto.getNombre());
        infractor.setApellido(dto.getApellido());
        infractor.setEmail(dto.getEmail());
        infractor.setBloqueado(false);
        return mapToResponse(infractorRepository.save(infractor));
    }

    @Override
    public InfractorResponseDTO obtenerInfractorPorId(Long id) {
        Infractor infractor = infractorRepository.findById(id)
                .orElseThrow(() -> new InfractorNotFoundException(id));
        return mapToResponse(infractor);
    }

    @Override
    public List<InfractorResponseDTO> obtenerTodos() {
        return infractorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void asignarVehiculo(Long infractorId, Long vehiculoId) {
        Infractor infractor = infractorRepository.findById(infractorId)
                .orElseThrow(() -> new InfractorNotFoundException(infractorId));
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));
        infractor.getVehiculos().add(vehiculo);
        infractorRepository.save(infractor);
    }

    @Override
    public Double calcularDeuda(Long infractorId) {
        // Validar que el infractor existe
        infractorRepository.findById(infractorId)
                .orElseThrow(() -> new InfractorNotFoundException(infractorId));


        List<EstadoMulta> estados = List.of(EstadoMulta.PENDIENTE, EstadoMulta.VENCIDA);
        List<Multa> multas = multaRepository.findByInfractor_IdAndEstadoIn(infractorId, estados);

        // Calcular deuda total
        double deudaTotal = 0.0;
        for (Multa multa : multas) {
            if (multa.getEstado() == EstadoMulta.VENCIDA) {

                deudaTotal += multa.getMonto() * 1.15;
            }  else {
                deudaTotal += multa.getMonto();
            }
        }

        return deudaTotal;
    }

    @Override
    public void desasignarVehiculo(Long infractorId, Long vehiculoId) {
        // Validar que el infractor existe
        Infractor infractor = infractorRepository.findById(infractorId)
                .orElseThrow(() -> new InfractorNotFoundException(infractorId));


        // Validar que el vehículo existe
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));
        // Validar que el vehículo está asignado al infractor
        if (!infractor.getVehiculos().contains(vehiculo)) {
            throw new VehiculoNotFoundException(vehiculoId);
        }



        long multasPendientes = multaRepository.countByVehiculo_IdAndEstado(
                vehiculoId, EstadoMulta.PENDIENTE);

        if (multasPendientes > 0) {
            throw new IllegalStateException(
                    "No se puede desasignar el vehículo xd. Tiene " + multasPendientes + " multa(s) pendiente(s)");
        }


        infractor.getVehiculos().remove(vehiculo);
        infractorRepository.save(infractor);




        if (vehiculo.isSuspendido()) {
            long multasActivas = multaRepository.countByVehiculo_IdAndEstado(
                    vehiculoId, EstadoMulta.PENDIENTE);
            if (multasActivas == 0) {
                vehiculo.setSuspendido(false);
                vehiculoRepository.save(vehiculo);
            }
        }
    }

    private InfractorResponseDTO mapToResponse(Infractor infractor) {
        InfractorResponseDTO dto = new InfractorResponseDTO();
        dto.setId(infractor.getId());
        dto.setDni(infractor.getDni());
        dto.setNombre(infractor.getNombre());
        dto.setApellido(infractor.getApellido());
        dto.setEmail(infractor.getEmail());
        dto.setBloqueado(infractor.isBloqueado());
        return dto;
    }
}