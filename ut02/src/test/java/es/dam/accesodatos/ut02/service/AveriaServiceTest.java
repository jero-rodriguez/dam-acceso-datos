package es.dam.accesodatos.ut02.service;

import es.dam.accesodatos.ut02.entity.Averia;
import es.dam.accesodatos.ut02.entity.EstadoReparacion;
import es.dam.accesodatos.ut02.entity.Vehiculo;
import es.dam.accesodatos.ut02.repository.AveriaRepository;
import es.dam.accesodatos.ut02.repository.VehiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AveriaServiceTest {

    @Mock
    private AveriaRepository averiaRepository;
    @Mock
    private VehiculoRepository vehiculoRepository;
    @InjectMocks
    private AveriaService service;

    @Test
    void listarAveriasDevuelveTodas() {
        List<Averia> averias = List.of(new Averia(), new Averia());
        when(averiaRepository.findAll()).thenReturn(averias);

        assertSame(averias, service.listarAverias());
        verify(averiaRepository).findAll();
    }

    @Test
    void obtenerAveriaDelegadaAlRepositorio() {
        Averia averia = new Averia();
        when(averiaRepository.findById(7L)).thenReturn(Optional.of(averia));

        assertSame(averia, service.obtenerAveria(7L).orElseThrow());
    }

    @Test
    void crearAveriaAsociaVehiculoYUsaEstadoPorDefecto() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(3L);
        Averia datos = new Averia();
        datos.setDescripcion("Freno");
        datos.setVehiculo(vehiculo);
        when(vehiculoRepository.findById(3L)).thenReturn(Optional.of(vehiculo));
        when(averiaRepository.save(any(Averia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Averia creada = service.crearAveria(datos).orElseThrow();

        assertEquals("Freno", creada.getDescripcion());
        assertEquals(EstadoReparacion.PENDIENTE, creada.getEstado());
        assertSame(vehiculo, creada.getVehiculo());
        assertTrue(vehiculo.getAverias().contains(creada));
        verify(averiaRepository).save(creada);
    }

    @Test
    void crearAveriaConVehiculoInexistenteDevuelveVacioYSinGuardar() {
        Averia datos = new Averia();
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(99L);
        datos.setVehiculo(vehiculo);
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.crearAveria(datos).isEmpty());
        verify(averiaRepository, never()).save(any());
    }

    @Test
    void eliminarAveriaSinVehiculoAsociadoDevuelveFalseYSinEliminar() {
        Averia averia = new Averia();
        when(averiaRepository.findById(10L)).thenReturn(Optional.of(averia));

        assertFalse(service.eliminarAveria(10L));
        verify(averiaRepository, never()).delete(any());
    }

    @Test
    void actualizarAveriaModificaYGuarda() {
        Averia existente = new Averia();
        Averia datos = new Averia();
        datos.setDescripcion("Motor");
        datos.setEstado(EstadoReparacion.EN_REPARACION);
        when(averiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(averiaRepository.save(existente)).thenReturn(existente);

        assertSame(existente, service.actualizarAveria(1L, datos).orElseThrow());
        assertEquals("Motor", existente.getDescripcion());
        assertEquals(EstadoReparacion.EN_REPARACION, existente.getEstado());
        verify(averiaRepository).save(existente);
    }

    @Test
    void eliminarAveriaExistenteLaDesvincula() {
        Vehiculo vehiculo = new Vehiculo();
        Averia averia = new Averia();
        vehiculo.addAveria(averia);
        when(averiaRepository.findById(1L)).thenReturn(Optional.of(averia));

        assertTrue(service.eliminarAveria(1L));
        assertTrue(vehiculo.getAverias().isEmpty());
        assertNull(averia.getVehiculo());
        verify(averiaRepository, never()).delete(any());
    }

    @Test
    void eliminarAveriaInexistenteDevuelveFalse() {
        when(averiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertFalse(service.eliminarAveria(1L));
    }
}
