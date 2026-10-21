package sv.edu.esfe.barberia.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "servicios",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_servicio_nombre", columnNames = "nombre_clave"))
public class ServicioEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, length = 100)
  public String nombre;

  @Column(name = "nombre_clave", nullable = false, length = 100)
  public String nombreClave;

  @Column(nullable = false, precision = 6, scale = 2)
  public BigDecimal precio;

  @Column(name = "duracion_minutos", nullable = false)
  public int duracionMinutos;

  public ServicioEntity() {}
}
