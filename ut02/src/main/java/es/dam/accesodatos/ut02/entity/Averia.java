package es.dam.accesodatos.ut02.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "averias")
@Getter
@Setter
@NoArgsConstructor
public class Averia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReparacion estado =
            EstadoReparacion.PENDIENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @PrePersist
    protected void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();

        fechaCreacion = ahora;
        fechaUltimaActualizacion = ahora;

        if (estado == null) {
            estado = EstadoReparacion.PENDIENTE;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        fechaUltimaActualizacion = LocalDateTime.now();
    }
}
