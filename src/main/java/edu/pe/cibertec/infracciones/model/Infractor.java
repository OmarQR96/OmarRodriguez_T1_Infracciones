package edu.pe.cibertec.infracciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "infractores")
public class Infractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean bloqueado = false;

    @ManyToMany
    @JoinTable(
            name = "infractor_vehiculo",
            joinColumns = @JoinColumn(name = "infractor_id"),
            inverseJoinColumns = @JoinColumn(name = "vehiculo_id")
    )
    private List<Vehiculo> vehiculos;
}