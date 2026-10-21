package sv.edu.esfe.barberia.application.port.in;

import java.util.List;
import sv.edu.esfe.barberia.domain.model.Servicio;

public interface ServicioUseCase {
  Servicio crear(Servicio entrada);

  Servicio actualizar(Long id, Servicio entrada);

  Servicio buscar(Long id);

  List<Servicio> listar();

  void eliminar(Long id);
}
