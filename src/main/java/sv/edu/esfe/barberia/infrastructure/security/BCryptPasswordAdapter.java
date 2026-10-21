package sv.edu.esfe.barberia.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sv.edu.esfe.barberia.application.port.out.PasswordPort;

@Component
public class BCryptPasswordAdapter implements PasswordPort {
  private final PasswordEncoder encoder;

  public BCryptPasswordAdapter(PasswordEncoder encoder) {
    this.encoder = encoder;
  }

  public boolean coincide(String texto, String hash) {
    return encoder.matches(texto, hash);
  }
}
