package es.dam.accesodatos.ut02.controller;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.service.VehiculoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoControllerTest {
    @Mock private VehiculoService service;
    @InjectMocks private VehiculoController controller;

    @Test void listarYObtenerTraducenResultados() {
        List<Vehiculo> vehiculos = List.of(new Vehiculo()); when(service.listarVehiculos()).thenReturn(vehiculos);
        assertSame(vehiculos, controller.listar().getBody());
        Vehiculo vehiculo = new Vehiculo(); when(service.obtenerVehiculo(1L)).thenReturn(Optional.of(vehiculo));
        assertEquals(HttpStatus.OK, controller.obtener(1L).getStatusCode()); assertSame(vehiculo, controller.obtener(1L).getBody());
    }

    @Test void obtenerInexistenteDevuelveNotFound() {
        when(service.obtenerVehiculo(1L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.obtener(1L).getStatusCode());
    }

    @Test void crearDevuelveCreated() {
        Vehiculo datos = new Vehiculo(); Vehiculo creado = new Vehiculo(); when(service.crearVehiculo(datos)).thenReturn(creado);
        ResponseEntity<Vehiculo> response = controller.crear(datos);
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); assertSame(creado, response.getBody());
    }

    @Test void actualizarDevuelveOkONotFound() {
        Vehiculo datos = new Vehiculo(); when(service.actualizarVehiculo(1L, datos)).thenReturn(Optional.of(datos));
        assertEquals(HttpStatus.OK, controller.actualizar(1L, datos).getStatusCode());
        when(service.actualizarVehiculo(2L, datos)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.actualizar(2L, datos).getStatusCode());
    }

    @Test void eliminarUsaElHttpStatusDelServicio() {
        when(service.eliminarVehiculo(1L)).thenReturn(HttpStatus.NO_CONTENT);
        assertEquals(HttpStatus.NO_CONTENT, controller.eliminar(1L).getStatusCode());
        when(service.eliminarVehiculo(2L)).thenReturn(HttpStatus.CONFLICT);
        assertEquals(HttpStatus.CONFLICT, controller.eliminar(2L).getStatusCode());
    }

    @Test void listarAveriasDevuelveOkONotFound() {
        List<Averia> averias = List.of(new Averia()); when(service.listarAveriasVehiculo(1L)).thenReturn(Optional.of(averias));
        assertSame(averias, controller.listarAverias(1L).getBody());
        when(service.listarAveriasVehiculo(2L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, controller.listarAverias(2L).getStatusCode());
    }
}
