package es.dam.accesodatos.ut02.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private String modelo;

    private LocalDate fechaMatriculacion;

    @OneToMany(
            mappedBy = "vehiculo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Averia> averias = new ArrayList<>();

    public void addAveria(Averia averia) {
        averias.add(averia);
        averia.setVehiculo(this);
    }

    public void removeAveria(Averia averia) {
        averias.remove(averia);
        averia.setVehiculo(null);
    }
}
