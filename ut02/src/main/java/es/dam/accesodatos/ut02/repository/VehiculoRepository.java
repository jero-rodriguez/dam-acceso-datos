package es.dam.accesodatos.ut02.repository;

import es.dam.accesodatos.ut02.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
}
