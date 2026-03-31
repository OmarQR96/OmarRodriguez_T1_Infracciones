package edu.pe.cibertec.infracciones.repository;

import edu.pe.cibertec.infracciones.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByMulta_Id(Long multaId);
    List<Pago> findByMulta_Infractor_Id(Long infractorId);
    boolean existsByMulta_Id(Long multaId);
}