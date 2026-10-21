package sv.edu.esfe.barberia.infrastructure.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.esfe.barberia.infrastructure.persistence.entity.UsuarioEntity;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
  Optional<UsuarioEntity> findByEmail(String email);
}
