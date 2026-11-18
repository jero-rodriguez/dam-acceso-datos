package es.dam.accesodatos.ut02.service;
import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.EstadoReparacion;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.repository.AveriaRepository;
import es.dam.accesodatos.ut02.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AveriaService {

    private final AveriaRepository averiaRepository;
    private final VehiculoRepository vehiculoRepository;

    // Listar todas las averías
    @Transactional(readOnly = true)
    public List<Averia> listarAverias() {
        return averiaRepository.findAll();
    }

    // Obtener avería por ID
    @Transactional(readOnly = true)
    public Optional<Averia> obtenerAveria(Long id) {
        return averiaRepository.findById(id);
    }

    // Crear avería asociada a un vehículo existente
    @Transactional
    public Optional<Averia> crearAveria(Averia datos) {

        Long vehiculoId = datos.getVehiculo().getId();

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

                    return averiaRepository.save(averia);
                });
    }

    // Actualizar únicamente descripción y estado
    @Transactional
    public Optional<Averia> actualizarAveria(
            Long id, Averia datos) {

        return averiaRepository.findById(id)
                .map(averia -> {

                    averia.setDescripcion(datos.getDescripcion());
                    averia.setEstado(datos.getEstado());

                    return averiaRepository.save(averia);
                });
    }

    // Eliminar avería y sincronizar la relación
    @Transactional
    public boolean eliminarAveria(Long id) {

        Optional<Averia> resultado =
                averiaRepository.findById(id);

        if (resultado.isEmpty()) {
            return false;
        }

        Averia averia = resultado.get();
        Vehiculo vehiculo = averia.getVehiculo();

        // orphanRemoval elimina la avería de la BD
        vehiculo.removeAveria(averia);

        return true;
    }
}

