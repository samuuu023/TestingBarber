package sv.edu.esfe.barberia.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, unique = true, length = 160)
  public String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  public String passwordHash;

  @Column(nullable = false, length = 20)
  public String rol;

  public UsuarioEntity() {}
}
