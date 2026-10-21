package sv.edu.esfe.barberia.domain.exception;

public class DuplicadoException extends RuntimeException {
  public DuplicadoException(String mensaje) {
    super(mensaje);
  }
}
