package sv.edu.esfe.barberia.infrastructure.rest;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import sv.edu.esfe.barberia.domain.model.Servicio;

public record ServicioRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotNull @DecimalMin("0") @DecimalMax("9999.99") @Digits(integer = 4, fraction = 2)
        BigDecimal precio,
    @Min(15) @Max(240) int duracionMinutos) {
  public Servicio toDomain() {
    return new Servicio(null, nombre, precio, duracionMinutos);
  }
}
