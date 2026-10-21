package sv.edu.esfe.barberia.application.usecase;

import java.util.Locale;
import sv.edu.esfe.barberia.application.port.out.*;
import sv.edu.esfe.barberia.domain.exception.CredencialesException;

public class LoginService {
  private final UsuarioRepositoryPort usuarios;
  private final PasswordPort passwords;
  private final TokenPort tokens;

  public LoginService(UsuarioRepositoryPort u, PasswordPort p, TokenPort t) {
    usuarios = u;
    passwords = p;
    tokens = t;
  }

  public String ejecutar(String email, String password) {
    if (email == null || password == null) throw new CredencialesException();
    var usuario =
        usuarios
            .buscarPorEmail(email.strip().toLowerCase(Locale.ROOT))
            .orElseThrow(CredencialesException::new);
    if (!passwords.coincide(password, usuario.passwordHash())) throw new CredencialesException();
    return tokens.generar(usuario);
  }
}
