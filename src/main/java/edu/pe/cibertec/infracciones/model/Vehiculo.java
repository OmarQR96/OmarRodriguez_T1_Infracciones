package edu.pe.cibertec.infracciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private boolean suspendido = false;

    @ManyToMany(mappedBy = "vehiculos")
    private List<Infractor> infractores;
}