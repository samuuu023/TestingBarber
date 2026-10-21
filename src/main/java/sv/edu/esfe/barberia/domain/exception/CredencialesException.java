package sv.edu.esfe.barberia.domain.exception;

public class CredencialesException extends RuntimeException {
  public CredencialesException() {
    super("Credenciales incorrectas");
  }
}
