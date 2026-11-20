package es.dam.accesodatos.ut02.controller;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.service.AveriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AveriaControllerTest {
    @Mock
    private AveriaService service;
    @InjectMocks
    private AveriaController controller;

    @Test
    void listarDevuelveOk() {
        List<Averia> datos = List.of(new Averia());
        when(service.listarAverias()).thenReturn(datos);
        ResponseEntity<List<Averia>> response = controller.listar();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(datos, response.getBody());
    }

    @Test
    void obtenerDevuelveOkCuandoExiste() {
        Averia averia = new Averia();
        when(service.obtenerAveria(1L)).thenReturn(Optional.of(averia));
        assertSame(averia, controller.obtener(1L).getBody());
        assertEquals(HttpStatus.OK, controller.obtener(1L).getStatusCode());
    }

    @Test
    void obtenerDevuelveNotFoundCuandoNoExiste() {
        when(service.obtenerAveria(1L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.obtener(1L).getStatusCode());
    }

    @Test
    void crearSinVehiculoDevuelveBadRequestYSinLlamarServicio() {
        Averia averia = new Averia();
        assertEquals(HttpStatus.BAD_REQUEST, controller.crear(averia).getStatusCode());
        verifyNoInteractions(service);
    }

    @Test
    void crearDevuelveCreated() {
        Averia datos = new Averia();
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(2L);
        datos.setVehiculo(vehiculo);
        Averia creada = new Averia();
        when(service.crearAveria(datos)).thenReturn(Optional.of(creada));
        ResponseEntity<Averia> response = controller.crear(datos);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(creada, response.getBody());
    }

    @Test
    void crearDevuelveNotFoundSiNoSePuedeAsociar() {
        Averia datos = new Averia();
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(2L);
        datos.setVehiculo(vehiculo);
        when(service.crearAveria(datos)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.crear(datos).getStatusCode());
    }

    @Test
    void actualizarYEliminarTraducenElResultado() {
        Averia datos = new Averia();
        when(service.actualizarAveria(1L, datos)).thenReturn(Optional.of(datos));
        assertEquals(HttpStatus.OK, controller.actualizar(1L, datos).getStatusCode());
        when(service.eliminarAveria(1L)).thenReturn(true);
        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
    }

    @Test
    void actualizarYEliminarDevuelvenNotFoundCuandoNoExiste() {
        Averia datos = new Averia();
        when(service.actualizarAveria(1L, datos)).thenReturn(Optional.empty());
        when(service.eliminarAveria(1L)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, controller.actualizar(1L, datos).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.eliminar(1L).getStatusCode());
    }
}
