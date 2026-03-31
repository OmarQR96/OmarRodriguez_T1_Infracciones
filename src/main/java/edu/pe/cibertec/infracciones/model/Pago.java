package edu.pe.cibertec.infracciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double montoPagado;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Column(nullable = false)
    private Double descuentoAplicado;

    @Column(nullable = false)
    private Double recargo;

    @OneToOne
    @JoinColumn(name = "multa_id", nullable = false, unique = true)
    private Multa multa;
}