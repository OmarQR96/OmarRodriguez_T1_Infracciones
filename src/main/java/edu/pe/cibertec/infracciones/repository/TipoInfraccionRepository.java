package edu.pe.cibertec.infracciones.repository;

import edu.pe.cibertec.infracciones.model.TipoInfraccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoInfraccionRepository extends JpaRepository<TipoInfraccion, Long> {
    Optional<TipoInfraccion> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}