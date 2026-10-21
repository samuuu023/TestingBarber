package sv.edu.esfe.barberia.application.port.out;

import java.util.*;
import sv.edu.esfe.barberia.domain.model.Servicio;

public interface ServicioRepositoryPort {
  Servicio guardar(Servicio servicio);

  Optional<Servicio> buscar(Long id);

  List<Servicio> listar();

  boolean existeNombre(String nombre, Long excluirId);

  void eliminar(Long id);
}
