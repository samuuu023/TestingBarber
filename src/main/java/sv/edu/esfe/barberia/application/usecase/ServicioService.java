package sv.edu.esfe.barberia.application.usecase;

import java.util.List;
import sv.edu.esfe.barberia.application.port.in.ServicioUseCase;
import sv.edu.esfe.barberia.application.port.out.ServicioRepositoryPort;
import sv.edu.esfe.barberia.domain.exception.*;
import sv.edu.esfe.barberia.domain.model.Servicio;

/** Caso de uso aislable con Mockito; persistencia mediante puerto. */
public class ServicioService implements ServicioUseCase {
  private final ServicioRepositoryPort repo;

  public ServicioService(ServicioRepositoryPort repo) {
    this.repo = repo;
  }

  public Servicio crear(Servicio s) {
    if (repo.existeNombre(s.nombre(), null))
      throw new DuplicadoException("Ya existe un servicio con ese nombre");
    return repo.guardar(new Servicio(null, s.nombre(), s.precio(), s.duracionMinutos()));
  }

  public Servicio actualizar(Long id, Servicio s) {
    buscar(id);
    if (repo.existeNombre(s.nombre(), id))
      throw new DuplicadoException("Ya existe un servicio con ese nombre");
    return repo.guardar(new Servicio(id, s.nombre(), s.precio(), s.duracionMinutos()));
  }

  public Servicio buscar(Long id) {
    return repo.buscar(id).orElseThrow(() -> new NoEncontradoException("Servicio no encontrado"));
  }

  public List<Servicio> listar() {
    return repo.listar();
  }

  public void eliminar(Long id) {
    buscar(id);
    repo.eliminar(id);
  }
}
