package es.dam.accesodatos.ut02.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/** Traduce errores de integridad de la base de datos a respuestas HTTP útiles. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            SQLIntegrityConstraintViolationException.class
    })
    public ResponseEntity<ApiError> handleIntegrityViolation(Exception exception) {
        if (esDuplicadoDeMatricula(exception)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError(
                            HttpStatus.CONFLICT.value(),
                            "MATRICULA_DUPLICADA",
                            "Ya existe un vehículo con esa matrícula"
                    ));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        HttpStatus.CONFLICT.value(),
                        "CONFLICTO_DATOS",
                        "La operación no puede completarse porque incumple una restricción de datos"
                ));
    }

    private boolean esDuplicadoDeMatricula(Throwable exception) {
        for (Throwable actual = exception; actual != null; actual = actual.getCause()) {
            String mensaje = actual.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("matricula")) {
                return true;
            }
        }
        return false;
    }

    public record ApiError(int status, String code, String message) {
    }
}
