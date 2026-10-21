package sv.edu.esfe.barberia.application.port.out;

import sv.edu.esfe.barberia.domain.model.Usuario;

public interface TokenPort {
  String generar(Usuario usuario);
}
