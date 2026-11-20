package es.dam.accesodatos.ut02.service;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.EstadoReparacion;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    // Listar todos los vehículos
    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos() {
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        log.debug("Vehículos listados: {}", vehiculos.size());
        return vehiculos;
    }

    // Obtener vehículo por ID
    @Transactional(readOnly = true)
    public Optional<Vehiculo> obtenerVehiculo(Long id) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(id);
        log.debug("Consulta de vehículo {}: {}", id, vehiculo.isPresent() ? "encontrado" : "no encontrado");
        return vehiculo;
    }

    // Crear vehículo
    @Transactional
    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        log.info("Creando vehículo con matrícula {}", vehiculo.getMatricula());
        Vehiculo nuevo = new Vehiculo();

        nuevo.setMatricula(vehiculo.getMatricula());
        nuevo.setModelo(vehiculo.getModelo());
        nuevo.setFechaMatriculacion(
                vehiculo.getFechaMatriculacion()
        );

        Vehiculo guardado = vehiculoRepository.save(nuevo);
        log.info("Vehículo creado con id {}", guardado.getId());
        return guardado;
    }

    // Actualizar vehículo
    @Transactional
    public Optional<Vehiculo> actualizarVehiculo(
            Long id, Vehiculo datos) {

        log.info("Actualizando vehículo {}", id);
        return vehiculoRepository.findById(id)
                .map(vehiculo -> {

                    vehiculo.setMatricula(datos.getMatricula());
                    vehiculo.setModelo(datos.getModelo());
                    vehiculo.setFechaMatriculacion(
                            datos.getFechaMatriculacion()
                    );

                    Vehiculo actualizado = vehiculoRepository.save(vehiculo);
                    log.info("Vehículo {} actualizado", id);
                    return actualizado;
                });
    }

    // Listar las averías de un vehículo
    @Transactional(readOnly = true)
    public Optional<List<Averia>> listarAveriasVehiculo(Long id) {

        log.debug("Listando averías del vehículo {}", id);
        return vehiculoRepository.findById(id)
                .map(vehiculo -> {
                    List<Averia> averias = List.copyOf(vehiculo.getAverias());
                    log.debug("El vehículo {} tiene {} averías", id, averias.size());
                    return averias;
                })
                .or(() -> {
                    log.warn("No se pueden listar las averías: no existe el vehículo {}", id);
                    return Optional.empty();
                });
    }

    // Eliminar vehículo según el estado de sus averías
    @Transactional
    public HttpStatus eliminarVehiculo(Long id) {

        Optional<Vehiculo> resultado =
                vehiculoRepository.findById(id);

        if (resultado.isEmpty()) {
            log.warn("No se puede eliminar el vehículo {}: no existe", id);
            return HttpStatus.NOT_FOUND;
        }

        Vehiculo vehiculo = resultado.get();

        boolean tieneAveriasActivas =
                vehiculo.getAverias().stream()
                        .anyMatch(averia ->
                                averia.getEstado() !=
                                        EstadoReparacion.TERMINADO
                        );

        if (tieneAveriasActivas) {
            log.warn("No se puede eliminar el vehículo {}: tiene averías activas", id);
            return HttpStatus.CONFLICT;
        }

        vehiculoRepository.delete(vehiculo);
        log.info("Vehículo {} eliminado", id);

        return HttpStatus.NO_CONTENT;
    }
}

