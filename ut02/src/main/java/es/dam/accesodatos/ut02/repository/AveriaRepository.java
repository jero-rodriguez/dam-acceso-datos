package es.dam.accesodatos.ut02.repository;

import es.dam.accesodatos.ut02.entity.Averia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AveriaRepository extends JpaRepository<Averia, Long> {

    List<Averia> findByVehiculoId(Long vehiculoId);

}
