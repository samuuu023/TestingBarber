package sv.edu.esfe.barberia.infrastructure.persistence.adapter;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import sv.edu.esfe.barberia.application.port.out.UsuarioRepositoryPort;
import sv.edu.esfe.barberia.domain.model.Usuario;
import sv.edu.esfe.barberia.infrastructure.persistence.repository.UsuarioJpaRepository;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {
  private final UsuarioJpaRepository jpa;

  public UsuarioRepositoryAdapter(UsuarioJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<Usuario> buscarPorEmail(String email) {
    return jpa.findByEmail(email).map(e -> new Usuario(e.id, e.email, e.passwordHash, e.rol));
  }
}
