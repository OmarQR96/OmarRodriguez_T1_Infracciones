package edu.pe.cibertec.infracciones.impl;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.exception.MultaNotFoundException;
import edu.pe.cibertec.infracciones.model.*;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MultaServiceImpl - Unit Test")
class MultaServiceImplTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    private Infractor infractorA;
    private Infractor infractorB;
    private Vehiculo vehiculo;
    private Multa multaPendiente;
    private TipoInfraccion tipoInfraccion;

    @BeforeEach
    void setUp() {
        // Configurar tipo de infracción
        tipoInfraccion = new TipoInfraccion();
        tipoInfraccion.setId(1L);
        tipoInfraccion.setCodigo("TI-001");
        tipoInfraccion.setDescripcion("Exceso de velocidad");
        tipoInfraccion.setMontoBase(500.00);

        // Configurar vehículo
        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setMarca("Toyota");
        vehiculo.setAnio(2020);
        vehiculo.setSuspendido(false);

        // Configurar infractor A (original)
        infractorA = new Infractor();
        infractorA.setId(1L);
        infractorA.setDni("12345678");
        infractorA.setNombre("Juan");
        infractorA.setApellido("Pérez");
        infractorA.setEmail("juan.perez@mail.com");
        infractorA.setBloqueado(false);
        infractorA.setVehiculos(new ArrayList<>(List.of(vehiculo)));

        // Configurar infractor B (nuevo - NO bloqueado)
        infractorB = new Infractor();
        infractorB.setId(2L);
        infractorB.setDni("87654321");
        infractorB.setNombre("María");
        infractorB.setApellido("García");
        infractorB.setEmail("maria.garcia@mail.com");
        infractorB.setBloqueado(false);
        infractorB.setVehiculos(new ArrayList<>(List.of(vehiculo)));

        // Configurar multa PENDIENTE
        multaPendiente = new Multa();
        multaPendiente.setId(1L);
        multaPendiente.setCodigo("MUL-001");
        multaPendiente.setMonto(500.00);
        multaPendiente.setEstado(EstadoMulta.PENDIENTE);
        multaPendiente.setFechaEmision(LocalDate.now());
        multaPendiente.setFechaVencimiento(LocalDate.now().plusDays(30));
        multaPendiente.setInfractor(infractorA);
        multaPendiente.setVehiculo(vehiculo);
        // ✅ CORREGIDO: Inicializar tiposInfraccion como lista vacía
        multaPendiente.setTiposInfraccion(new ArrayList<>(List.of(tipoInfraccion)));
    }

    // ========================================================================
    // PREGUNTA 3: TRANSFERIR MULTA
    // ========================================================================
    @Test
    @DisplayName("Should transfer fine successfully to non-blocked infractor with same vehicle")
    void shouldTransferMultaSuccessfully() {
        // GIVEN
        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaPendiente));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(infractorB));
        when(multaRepository.save(any(Multa.class))).thenReturn(multaPendiente);

        // WHEN
        var response = multaService.transferirMulta(1L, 2L);

        // THEN
        assertNotNull(response);
        verify(multaRepository, times(1)).save(any(Multa.class));
        assertEquals(infractorB, multaPendiente.getInfractor());
    }

    @Test
    @DisplayName("Should throw exception when multa not found")
    void shouldThrowExceptionWhenMultaNotFound() {
        // GIVEN
        when(multaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(MultaNotFoundException.class, () -> {
            multaService.transferirMulta(99L, 2L);
        });
    }

    @Test
    @DisplayName("Should throw exception when new infractor is blocked")
    void shouldThrowExceptionWhenInfractorBloqueado() {
        // GIVEN - infractor B está BLOQUEADO
        infractorB.setBloqueado(true);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaPendiente));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(infractorB));

        // WHEN & THEN
        assertThrows(InfractorBloqueadoException.class, () -> {
            multaService.transferirMulta(1L, 2L);
        });

        verify(multaRepository, never()).save(any(Multa.class));
    }

    @Test
    @DisplayName("Should throw exception when fine is not PENDING")
    void shouldThrowExceptionWhenFineNotPending() {
        // GIVEN - multa está PAGADA
        multaPendiente.setEstado(EstadoMulta.PAGADA);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaPendiente));
        // ✅ CORREGIDO: Remover stubbing innecesario de infractorRepository

        // WHEN & THEN
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            multaService.transferirMulta(1L, 2L);
        });

        assertTrue(exception.getMessage().contains("PENDIENTE"));
        verify(multaRepository, never()).save(any(Multa.class));
    }

    // ========================================================================
    // PREGUNTA 4: TEST AVANZADO CON ArgumentCaptor y verify()
    // ========================================================================
    @Test
    @DisplayName("Should throw InfractorBloqueadoException and NOT call save() when infractor is blocked")
    void shouldThrowExceptionAndNotSaveWhenInfractorBloqueado() {
        // GIVEN
        infractorB.setBloqueado(true);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaPendiente));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(infractorB));

        // ArgumentCaptor para capturar el objeto Multa
        ArgumentCaptor<Multa> multaCaptor = ArgumentCaptor.forClass(Multa.class);

        // WHEN & THEN
        InfractorBloqueadoException exception = assertThrows(
                InfractorBloqueadoException.class,
                () -> multaService.transferirMulta(1L, 2L)
        );

        // Verificar que el mensaje contiene el ID
        assertTrue(exception.getMessage().contains("2"));

        // VERIFICAR CON verify() que save() NO fue llamado
        verify(multaRepository, never()).save(any(Multa.class));

        // Verificación con ArgumentCaptor
        verify(multaRepository, times(0)).save(multaCaptor.capture());
        assertTrue(multaCaptor.getAllValues().isEmpty());
    }

    @Test
    @DisplayName("Should capture Multa object when transfer is successful")
    void shouldCaptureMultaObjectWhenTransferSuccessful() {
        // GIVEN
        infractorB.setBloqueado(false);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multaPendiente));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(infractorB));
        when(multaRepository.save(any(Multa.class))).thenReturn(multaPendiente);

        // ArgumentCaptor
        ArgumentCaptor<Multa> multaCaptor = ArgumentCaptor.forClass(Multa.class);

        // WHEN
        multaService.transferirMulta(1L, 2L);

        // THEN
        verify(multaRepository, times(1)).save(multaCaptor.capture());

        Multa multaGuardada = multaCaptor.getValue();
        assertNotNull(multaGuardada);
        assertEquals(2L, multaGuardada.getInfractor().getId());
    }
}