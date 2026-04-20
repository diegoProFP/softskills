package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.exception.CursoYaRegistradoException;
import es.ggm.infor.softskills.exception.GrupoNoResueltoException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(CursoYaRegistradoException.class)
    public ResponseEntity<String> handleCursoYaRegistrado(CursoYaRegistradoException e) {
        logger.error("Error funcional al registrar curso", e);
        return ResponseEntity.badRequest().body("El curso ya ha sido registrado.");
    }

    @ExceptionHandler(GrupoNoResueltoException.class)
    public ResponseEntity<String> handleGrupoNoResuelto(GrupoNoResueltoException e) {
        logger.error("Error al asociar curso con grupo", e);
        return ResponseEntity.badRequest().body("Se ha producido un error al asociar el curso, consulte a su administrador.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException e) {
        logger.error("Recurso no encontrado", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se ha encontrado el recurso solicitado.");
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleEntityExists(EntityExistsException e) {
        logger.error("Recurso duplicado", e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body("El recurso ya existe.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException e) {
        logger.error("Solicitud no valida", e);
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("La solicitud no es valida.");
        return ResponseEntity.badRequest().body(mensaje);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
        logger.error("Acceso denegado", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos para realizar esta operación.");
    }

    @ExceptionHandler(GeneralMoodleException.class)
    public ResponseEntity<String> handleGeneralMoodle(GeneralMoodleException e) {
        logger.error("Error al comunicarse con Moodle", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Se ha producido un error interno, consulte a su administrador.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        logger.error("Error no controlado en la API", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Se ha producido un error interno, consulte a su administrador.");
    }
}
