package es.dam.accesodatos.ut02.controller;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.service.AveriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/averias")
@RequiredArgsConstructor
public class AveriaController {

    private final AveriaService averiaService;

    // Listar todas las averías
    @GetMapping
    public ResponseEntity<List<Averia>> listar() {
        return ResponseEntity.ok(averiaService.listarAverias());
    }

    // Obtener avería por ID
    @GetMapping("/{id}")
    public ResponseEntity<Averia> obtener(@PathVariable("id") Long id) {
        return averiaService.obtenerAveria(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear avería
    @PostMapping
    public ResponseEntity<Averia> crear(@RequestBody Averia averia) {

        if (averia.getVehiculo() == null ||
                averia.getVehiculo().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        return averiaService.crearAveria(averia)
                .map(creada -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(creada))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Modificar avería
    @PutMapping("/{id}")
    public ResponseEntity<Averia> actualizar(
            @PathVariable("id") Long id,
            @RequestBody Averia averia) {

        return averiaService.actualizarAveria(id, averia)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar avería
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {

        if (averiaService.eliminarAveria(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
