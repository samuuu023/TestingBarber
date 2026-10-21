package sv.edu.esfe.barberia.infrastructure.persistence.adapter;

import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.esfe.barberia.application.port.out.ServicioRepositoryPort;
import sv.edu.esfe.barberia.domain.model.Servicio;
import sv.edu.esfe.barberia.infrastructure.persistence.entity.ServicioEntity;
import sv.edu.esfe.barberia.infrastructure.persistence.repository.ServicioJpaRepository;

@Repository
@Transactional
public class ServicioRepositoryAdapter implements ServicioRepositoryPort {
  private final ServicioJpaRepository jpa;

  public ServicioRepositoryAdapter(ServicioJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Servicio guardar(Servicio s) {
    var e = new ServicioEntity();
    e.id = s.id();
    e.nombre = s.nombre();
    e.nombreClave = s.nombre().toLowerCase(Locale.ROOT);
    e.precio = s.precio();
    e.duracionMinutos = s.duracionMinutos();
    return map(jpa.saveAndFlush(e));
  }

  public Optional<Servicio> buscar(Long id) {
    return jpa.findById(id).map(this::map);
  }

  public List<Servicio> listar() {
    return jpa.findAll(Sort.by("id")).stream().map(this::map).toList();
  }

  public boolean existeNombre(String nombre, Long id) {
    String key = nombre.toLowerCase(Locale.ROOT);
    return id == null ? jpa.existsByNombreClave(key) : jpa.existsByNombreClaveAndIdNot(key, id);
  }

  public void eliminar(Long id) {
    jpa.deleteById(id);
    jpa.flush();
  }

  private Servicio map(ServicioEntity e) {
    return new Servicio(e.id, e.nombre, e.precio, e.duracionMinutos);
  }
}
