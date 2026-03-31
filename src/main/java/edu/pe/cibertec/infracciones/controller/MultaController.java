package edu.pe.cibertec.infracciones.controller;

import edu.pe.cibertec.infracciones.dto.MultaRequestDTO;
import edu.pe.cibertec.infracciones.dto.MultaResponseDTO;
import edu.pe.cibertec.infracciones.service.IMultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/multas")
@RequiredArgsConstructor
public class MultaController {

    private final IMultaService multaService;

    @PostMapping
    public ResponseEntity<MultaResponseDTO> registrar(@RequestBody MultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(multaService.registrarMulta(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(multaService.obtenerMultaPorId(id));
    }

    @GetMapping("/infractor/{infractorId}")
    public ResponseEntity<List<MultaResponseDTO>> obtenerPorInfractor(@PathVariable Long infractorId) {
        return ResponseEntity.ok(multaService.obtenerMultasPorInfractor(infractorId));
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<MultaResponseDTO>> obtenerPorVehiculo(@PathVariable Long vehiculoId) {
        return ResponseEntity.ok(multaService.obtenerMultasPorVehiculo(vehiculoId));
    }
}