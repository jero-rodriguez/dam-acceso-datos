package es.dam.accesodatos.ut02.service;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.EstadoReparacion;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.repository.AveriaRepository;
import es.dam.accesodatos.ut02.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AveriaService {

    private final AveriaRepository averiaRepository;
    private final VehiculoRepository vehiculoRepository;

    // Listar todas las averías
    @Transactional(readOnly = true)
    public List<Averia> listarAverias() {
        List<Averia> averias = averiaRepository.findAll();
        log.debug("Averías listadas: {}", averias.size());
        return averias;
    }

    // Obtener avería por ID
    @Transactional(readOnly = true)
    public Optional<Averia> obtenerAveria(Long id) {
        Optional<Averia> averia = averiaRepository.findById(id);
        log.debug("Consulta de avería {}: {}", id, averia.isPresent() ? "encontrada" : "no encontrada");
        return averia;
    }

    // Crear avería asociada a un vehículo existente
    @Transactional
    public Optional<Averia> crearAveria(Averia datos) {

        Long vehiculoId = datos.getVehiculo().getId();
        log.info("Creando avería para el vehículo {}", vehiculoId);

        return vehiculoRepository.findById(vehiculoId)
                .map(vehiculo -> {

                    Averia averia = new Averia();

                    averia.setDescripcion(datos.getDescripcion());

                    averia.setEstado(
                            datos.getEstado() != null
                                    ? datos.getEstado()
                                    : EstadoReparacion.PENDIENTE
                    );

                    // Sincronizar la relación bidireccional
                    vehiculo.addAveria(averia);

                    Averia guardada = averiaRepository.save(averia);
                    log.info("Avería creada con id {} para el vehículo {}", guardada.getId(), vehiculoId);
                    return guardada;
                })
                .or(() -> {
                    log.warn("No se puede crear la avería: no existe el vehículo {}", vehiculoId);
                    return Optional.empty();
                });
    }

    // Actualizar únicamente descripción y estado
    @Transactional
    public Optional<Averia> actualizarAveria(
            Long id, Averia datos) {

        log.info("Actualizando avería {}", id);
        return averiaRepository.findById(id)
                .map(averia -> {

                    averia.setDescripcion(datos.getDescripcion());
                    averia.setEstado(datos.getEstado());

                    Averia actualizada = averiaRepository.save(averia);
                    log.info("Avería {} actualizada", id);
                    return actualizada;
                });
    }

    // Eliminar avería y sincronizar la relación
    @Transactional
    public boolean eliminarAveria(Long id) {

        Optional<Averia> resultado =
                averiaRepository.findById(id);

        if (resultado.isEmpty()) {
            log.warn("No se puede eliminar la avería {}: no existe", id);
            return false;
        }

        Averia averia = resultado.get();
        Vehiculo vehiculo = averia.getVehiculo();

        if (vehiculo == null) {
            log.warn("No se puede eliminar la avería {}: no tiene un vehículo asociado", id);
            return false;
        }

        // orphanRemoval elimina la avería de la BD
        vehiculo.removeAveria(averia);
        log.info("Avería {} eliminada", id);

        return true;
    }
}

