package es.dam.accesodatos.ut02.service;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.EstadoReparacion;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    // Listar todos los vehículos
    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    // Obtener vehículo por ID
    @Transactional(readOnly = true)
    public Optional<Vehiculo> obtenerVehiculo(Long id) {
        return vehiculoRepository.findById(id);
    }

    // Crear vehículo
    @Transactional
    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        Vehiculo nuevo = new Vehiculo();

        nuevo.setMatricula(vehiculo.getMatricula());
        nuevo.setModelo(vehiculo.getModelo());
        nuevo.setFechaMatriculacion(
                vehiculo.getFechaMatriculacion()
        );

        return vehiculoRepository.save(nuevo);
    }

    // Actualizar vehículo
    @Transactional
    public Optional<Vehiculo> actualizarVehiculo(
            Long id, Vehiculo datos) {

        return vehiculoRepository.findById(id)
                .map(vehiculo -> {

                    vehiculo.setMatricula(datos.getMatricula());
                    vehiculo.setModelo(datos.getModelo());
                    vehiculo.setFechaMatriculacion(
                            datos.getFechaMatriculacion()
                    );

                    return vehiculoRepository.save(vehiculo);
                });
    }

    // Listar las averías de un vehículo
    @Transactional(readOnly = true)
    public Optional<List<Averia>> listarAveriasVehiculo(Long id) {

        return vehiculoRepository.findById(id)
                .map(vehiculo ->
                        List.copyOf(vehiculo.getAverias())
                );
    }

    // Eliminar vehículo según el estado de sus averías
    @Transactional
    public HttpStatus eliminarVehiculo(Long id) {

        Optional<Vehiculo> resultado =
                vehiculoRepository.findById(id);

        if (resultado.isEmpty()) {
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
            return HttpStatus.CONFLICT;
        }

        vehiculoRepository.delete(vehiculo);

        return HttpStatus.NO_CONTENT;
    }
}

