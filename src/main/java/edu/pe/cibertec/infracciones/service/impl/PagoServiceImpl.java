package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import edu.pe.cibertec.infracciones.exception.MultaNotFoundException;
import edu.pe.cibertec.infracciones.exception.PagoYaRealizadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.IPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final MultaRepository multaRepository;

    @Override
    public PagoResponseDTO procesarPago(Long multaId) {
        Multa multa = multaRepository.findById(multaId)
                .orElseThrow(() -> new MultaNotFoundException(multaId));

        if (multa.getEstado() == EstadoMulta.PAGADA)
            throw new PagoYaRealizadoException(multaId);

        LocalDate hoy = LocalDate.now();
        long diasDesdeEmision = ChronoUnit.DAYS.between(multa.getFechaEmision(), hoy);

        double monto = multa.getMonto();
        double descuento = diasDesdeEmision <= 5 ? monto * 0.20 : 0.0;
        double recargo = hoy.isAfter(multa.getFechaVencimiento()) ? monto * 0.15 : 0.0;
        double montoPagado = monto - descuento + recargo;

        Pago pago = new Pago();
        pago.setMontoPagado(montoPagado);
        pago.setFechaPago(hoy);
        pago.setDescuentoAplicado(descuento);
        pago.setRecargo(recargo);
        pago.setMulta(multa);
        pagoRepository.save(pago);

        multa.setEstado(EstadoMulta.PAGADA);
        multaRepository.save(multa);

        return mapToResponse(pago);
    }

    @Override
    public List<PagoResponseDTO> obtenerPagosPorInfractor(Long infractorId) {
        return pagoRepository.findByMulta_Infractor_Id(infractorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PagoResponseDTO mapToResponse(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setMontoPagado(pago.getMontoPagado());
        dto.setFechaPago(pago.getFechaPago());
        dto.setDescuentoAplicado(pago.getDescuentoAplicado());
        dto.setRecargo(pago.getRecargo());
        dto.setMultaId(pago.getMulta().getId());
        return dto;
    }
}