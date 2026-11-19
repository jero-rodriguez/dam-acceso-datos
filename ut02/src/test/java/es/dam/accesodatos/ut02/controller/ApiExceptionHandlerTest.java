package es.dam.accesodatos.ut02.controller;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void duplicadoDeMatriculaDevuelveConflictSinDetallesDeLaBaseDeDatos() {
        var sqlException = new SQLIntegrityConstraintViolationException(
                "Duplicate entry '4567ABC' for key 'vehiculos.matricula'");
        var exception = new DataIntegrityViolationException("fallo al guardar", sqlException);

        ResponseEntity<ApiExceptionHandler.ApiError> response =
                handler.handleIntegrityViolation(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("MATRICULA_DUPLICADA", response.getBody().code());
        assertEquals("Ya existe un vehículo con esa matrícula", response.getBody().message());
        assertEquals(409, response.getBody().status());
    }

    @Test
    void duplicadoDeMatriculaComoExcepcionJdbcTambienDevuelveConflict() {
        var exception = new SQLIntegrityConstraintViolationException(
                "Duplicate entry '4567ABC' for key 'vehiculos.matricula'");

        ResponseEntity<ApiExceptionHandler.ApiError> response =
                handler.handleIntegrityViolation(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("MATRICULA_DUPLICADA", response.getBody().code());
    }

    @Test
    void otraViolacionDeIntegridadDevuelveConflictoGenerico() {
        var exception = new DataIntegrityViolationException(
                "fallo de integridad",
                new RuntimeException("foreign key constraint"));

        ResponseEntity<ApiExceptionHandler.ApiError> response =
                handler.handleIntegrityViolation(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICTO_DATOS", response.getBody().code());
        assertEquals(
                "La operación no puede completarse porque incumple una restricción de datos",
                response.getBody().message());
    }
}
