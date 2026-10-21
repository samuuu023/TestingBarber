package sv.edu.esfe.barberia.domain.model;

import java.math.BigDecimal;
import sv.edu.esfe.barberia.domain.exception.ReglaNegocioException;

/** Dominio puro: no conoce Spring, JPA ni HTTP. */
public record Servicio(Long id, String nombre, BigDecimal precio, int duracionMinutos) {
  public Servicio {
    if (nombre == null || nombre.isBlank() || nombre.strip().length() > 100)
      throw new ReglaNegocioException("Nombre obligatorio, máximo 100 caracteres");
    nombre = nombre.strip();
    if (precio == null
        || precio.signum() < 0
        || precio.compareTo(new BigDecimal("9999.99")) > 0
        || precio.stripTrailingZeros().scale() > 2)
      throw new ReglaNegocioException("Precio entre 0 y 9999.99, máximo dos decimales");
    if (duracionMinutos < 15 || duracionMinutos > 240 || duracionMinutos % 15 != 0)
      throw new ReglaNegocioException("Duración de 15 a 240 en intervalos de 15 minutos");
  }
}
