package edu.pe.cibertec.infracciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "tipos_infraccion")
public class TipoInfraccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double montoBase;

    @ManyToMany(mappedBy = "tiposInfraccion")
    private List<Multa> multas;
}