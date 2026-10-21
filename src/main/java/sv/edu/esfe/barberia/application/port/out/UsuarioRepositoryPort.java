package sv.edu.esfe.barberia.application.port.out;

import java.util.Optional;
import sv.edu.esfe.barberia.domain.model.Usuario;

public interface UsuarioRepositoryPort {
  Optional<Usuario> buscarPorEmail(String email);
}
