package sv.edu.esfe.barberia.domain.exception;

public class NoEncontradoException extends RuntimeException {
  public NoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
