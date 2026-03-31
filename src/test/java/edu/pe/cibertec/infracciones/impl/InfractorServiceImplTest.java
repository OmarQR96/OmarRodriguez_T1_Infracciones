package edu.pe.cibertec.infracciones.impl;

import edu.pe.cibertec.infracciones.exception.InfractorNotFoundException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InfractorServiceImpl - Unit Test")
class InfractorServiceImplTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    private Infractor infractor;
    private Vehiculo vehiculo;
    private Multa multaPendiente;
    private Multa multaVencida;

    @BeforeEach
    void setUp() {
        // Configurar vehículo
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setMarca("Toyota");
        vehiculo.setAnio(2020);
        vehiculo.setSuspendido(false);


        infractor = new Infractor();
        infractor.setId(1L);
        infractor.setDni("12345678");
        infractor.setNombre("Juan");
        infractor.setApellido("Pérez");
        infractor.setEmail("juan.perez@mail.com");
        infractor.setBloqueado(false);
        infractor.setVehiculos(new ArrayList<>(List.of(vehiculo)));


        multaPendiente = new Multa();
        multaPendiente.setId(1L);
        multaPendiente.setCodigo("MUL-001");
        multaPendiente.setMonto(200.00);
        multaPendiente.setEstado(EstadoMulta.PENDIENTE);
        multaPendiente.setFechaEmision(LocalDate.now());
        multaPendiente.setFechaVencimiento(LocalDate.now().plusDays(30));
        multaPendiente.setInfractor(infractor);

        // Configurar multa VENCIDA (monto 300.00)
        multaVencida = new Multa();
        multaVencida.setId(2L);
        multaVencida.setCodigo("MUL-002");
        multaVencida.setMonto(300.00);
        multaVencida.setEstado(EstadoMulta.VENCIDA);
        multaVencida.setFechaEmision(LocalDate.now().minusDays(60));
        multaVencida.setFechaVencimiento(LocalDate.now().minusDays(30));
        multaVencida.setInfractor(infractor);
    }

    //pregunta1
    @Test
    @DisplayName("Should calculate debt successfully when infractor has PENDING and VENCIDA fines")
    void shouldCalculateDeudaSuccessfully() {
        // GIVEN
        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(multaRepository.findByInfractor_IdAndEstadoIn(eq(1L), any(List.class)))
                .thenReturn(List.of(multaPendiente, multaVencida));



        Double deuda = infractorService.calcularDeuda(1L);
        assertNotNull(deuda);
        assertEquals(545.00, deuda);
        verify(multaRepository, times(1))
                .findByInfractor_IdAndEstadoIn(eq(1L), any(List.class));
    }

    @Test
    @DisplayName("Should throw exception when infractor not found")
    void shouldThrowExceptionWhenInfractorNotFound() {



        when(infractorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InfractorNotFoundException.class, () -> {
            infractorService.calcularDeuda(99L);
        });
    }

    @Test
    @DisplayName("Should return 0.0 when infractor has no fines")
    void shouldReturnZeroWhenNoFines() {
        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(multaRepository.findByInfractor_IdAndEstadoIn(eq(1L), any(List.class)))
                .thenReturn(List.of());



        Double deuda = infractorService.calcularDeuda(1L);

        assertEquals(0.0, deuda);
    }

   //pregunta 2
    @Test
    @DisplayName("Should unassign vehicle successfully when no PENDING fines")
    void shouldDesasignarVehiculoSuccessfully() {

        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(multaRepository.countByVehiculo_IdAndEstado(1L, EstadoMulta.PENDIENTE))
                .thenReturn(0L);
        when(infractorRepository.save(any(Infractor.class))).thenReturn(infractor);

        assertDoesNotThrow(() -> {
            infractorService.desasignarVehiculo(1L, 1L);
        });

        verify(infractorRepository, times(1)).save(any(Infractor.class));
    }

    @Test
    @DisplayName("Should throw exception when vehicle has PENDING fines")
    void shouldThrowExceptionWhenVehicleHasPendingFines() {

        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(multaRepository.countByVehiculo_IdAndEstado(1L, EstadoMulta.PENDIENTE))
                .thenReturn(2L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            infractorService.desasignarVehiculo(1L, 1L);
        });


        assertTrue(exception.getMessage().contains("multa(s) pendiente(s)"));
        verify(infractorRepository, never()).save(any(Infractor.class));
    }
}