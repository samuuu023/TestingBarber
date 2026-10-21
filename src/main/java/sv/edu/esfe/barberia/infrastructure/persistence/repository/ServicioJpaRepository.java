package sv.edu.esfe.barberia.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.esfe.barberia.infrastructure.persistence.entity.ServicioEntity;

public interface ServicioJpaRepository extends JpaRepository<ServicioEntity, Long> {
  boolean existsByNombreClave(String nombre);

  boolean existsByNombreClaveAndIdNot(String nombre, Long id);
}
