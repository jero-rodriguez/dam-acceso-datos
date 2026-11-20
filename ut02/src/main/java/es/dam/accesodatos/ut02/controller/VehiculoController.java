package es.dam.accesodatos.ut02.controller;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // Listar todos los vehículos
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listar() {
        return ResponseEntity.ok(vehiculoService.listarVehiculos());
    }

    // Obtener vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtener(@PathVariable("id") Long id) {
        return vehiculoService.obtenerVehiculo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear vehículo
    @PostMapping
    public ResponseEntity<Vehiculo> crear(@RequestBody Vehiculo vehiculo) {
        Vehiculo creado = vehiculoService.crearVehiculo(vehiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Modificar vehículo
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizar(
            @PathVariable("id") Long id,
            @RequestBody Vehiculo vehiculo) {

        return vehiculoService.actualizarVehiculo(id, vehiculo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        HttpStatus resultado = vehiculoService.eliminarVehiculo(id);
        return ResponseEntity.status(resultado).build();
    }

    // Listar averías de un vehículo
    @GetMapping("/{id}/averias")
    public ResponseEntity<List<Averia>> listarAverias(
            @PathVariable("id") Long id) {

        return vehiculoService.listarAveriasVehiculo(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
