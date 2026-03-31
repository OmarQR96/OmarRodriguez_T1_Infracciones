package edu.pe.cibertec.infracciones.controller;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import edu.pe.cibertec.infracciones.service.IPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final IPagoService pagoService;

    @PostMapping("/{multaId}")
    public ResponseEntity<PagoResponseDTO> procesarPago(@PathVariable Long multaId) {
        return ResponseEntity.ok(pagoService.procesarPago(multaId));
    }

    @GetMapping("/infractor/{infractorId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPorInfractor(@PathVariable Long infractorId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorInfractor(infractorId));
    }
}