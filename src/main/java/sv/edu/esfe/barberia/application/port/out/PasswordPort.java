package sv.edu.esfe.barberia.application.port.out;

public interface PasswordPort {
  boolean coincide(String texto, String hash);
}
