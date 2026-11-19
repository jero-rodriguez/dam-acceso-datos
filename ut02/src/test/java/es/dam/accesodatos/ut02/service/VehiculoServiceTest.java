package es.dam.accesodatos.ut02.service;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.EstadoReparacion;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.repository.VehiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {
    @Mock private VehiculoRepository repository;
    @InjectMocks private VehiculoService service;

    @Test void listarVehiculosDevuelveTodos() {
        List<Vehiculo> datos = List.of(new Vehiculo());
        when(repository.findAll()).thenReturn(datos);
        assertSame(datos, service.listarVehiculos());
    }

    @Test void obtenerVehiculoDevuelveResultadoDelRepositorio() {
        Vehiculo vehiculo = new Vehiculo();
        when(repository.findById(1L)).thenReturn(Optional.of(vehiculo));
        assertSame(vehiculo, service.obtenerVehiculo(1L).orElseThrow());
    }

    @Test void crearVehiculoCopiaLosDatosYGuardaUnaNuevaInstancia() {
        Vehiculo datos = new Vehiculo();
        datos.setMatricula("1234ABC"); datos.setModelo("Corsa");
        datos.setFechaMatriculacion(LocalDate.of(2020, 1, 2));
        when(repository.save(any(Vehiculo.class))).thenAnswer(i -> i.getArgument(0));

        Vehiculo creado = service.crearVehiculo(datos);

        assertNotSame(datos, creado);
        assertEquals(datos.getMatricula(), creado.getMatricula());
        assertEquals(datos.getModelo(), creado.getModelo());
        assertEquals(datos.getFechaMatriculacion(), creado.getFechaMatriculacion());
    }

    @Test void actualizarVehiculoModificaYGuarda() {
        Vehiculo existente = new Vehiculo();
        Vehiculo datos = new Vehiculo(); datos.setMatricula("9999ZZZ"); datos.setModelo("Golf");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);
        assertSame(existente, service.actualizarVehiculo(1L, datos).orElseThrow());
        assertEquals("9999ZZZ", existente.getMatricula());
        assertEquals("Golf", existente.getModelo());
    }

    @Test void listarAveriasDevuelveCopiaInmodificable() {
        Vehiculo vehiculo = new Vehiculo(); vehiculo.addAveria(new Averia());
        when(repository.findById(1L)).thenReturn(Optional.of(vehiculo));
        List<Averia> resultado = service.listarAveriasVehiculo(1L).orElseThrow();
        assertEquals(1, resultado.size());
        assertThrows(UnsupportedOperationException.class, () -> resultado.clear());
    }

    @Test
    void listarAveriasDeVehiculoInexistenteDevuelveVacio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.listarAveriasVehiculo(99L).isEmpty());
    }

    @Test void eliminarVehiculoInexistenteDevuelveNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, service.eliminarVehiculo(1L));
    }

    @Test void eliminarVehiculoConAveriaActivaDevuelveConflict() {
        Vehiculo vehiculo = new Vehiculo(); Averia averia = new Averia();
        averia.setEstado(EstadoReparacion.EN_REPARACION); vehiculo.addAveria(averia);
        when(repository.findById(1L)).thenReturn(Optional.of(vehiculo));
        assertEquals(HttpStatus.CONFLICT, service.eliminarVehiculo(1L));
        verify(repository, never()).delete(any());
    }

    @Test void eliminarVehiculoSinAveriasActivasLoElimina() {
        Vehiculo vehiculo = new Vehiculo(); Averia averia = new Averia();
        averia.setEstado(EstadoReparacion.TERMINADO); vehiculo.addAveria(averia);
        when(repository.findById(1L)).thenReturn(Optional.of(vehiculo));
        assertEquals(HttpStatus.NO_CONTENT, service.eliminarVehiculo(1L));
        verify(repository).delete(vehiculo);
    }
}
