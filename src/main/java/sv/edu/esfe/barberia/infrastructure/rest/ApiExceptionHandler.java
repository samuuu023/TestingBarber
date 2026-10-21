package sv.edu.esfe.barberia.infrastructure.rest;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import sv.edu.esfe.barberia.domain.exception.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  private ResponseEntity<ProblemDetail> error(HttpStatus status, String message) {
    var p = ProblemDetail.forStatusAndDetail(status, message);
    return ResponseEntity.status(status).body(p);
  }

  @ExceptionHandler(NoEncontradoException.class)
  ResponseEntity<ProblemDetail> missing(NoEncontradoException e) {
    return error(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(DuplicadoException.class)
  ResponseEntity<ProblemDetail> duplicate(DuplicadoException e) {
    return error(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ProblemDetail> conflict() {
    return error(HttpStatus.CONFLICT, "Conflicto con un registro existente");
  }

  @ExceptionHandler(CredencialesException.class)
  ResponseEntity<ProblemDetail> credentials() {
    return error(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
  }

  @ExceptionHandler(ReglaNegocioException.class)
  ResponseEntity<ProblemDetail> business(ReglaNegocioException e) {
    return error(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    HttpMessageNotReadableException.class
  })
  ResponseEntity<ProblemDetail> invalid(Exception e) {
    return error(HttpStatus.BAD_REQUEST, "Datos inválidos: revise nombre, precio y duración");
  }
}
