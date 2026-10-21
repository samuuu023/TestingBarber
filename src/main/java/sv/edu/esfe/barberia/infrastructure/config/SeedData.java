package sv.edu.esfe.barberia.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.edu.esfe.barberia.infrastructure.persistence.entity.UsuarioEntity;
import sv.edu.esfe.barberia.infrastructure.persistence.repository.UsuarioJpaRepository;

@Configuration
public class SeedData {
  @Bean
  CommandLineRunner seed(
      UsuarioJpaRepository repo,
      PasswordEncoder encoder,
      @Value("${app.admin.email}") String email,
      @Value("${app.admin.password}") String password) {
    return args -> {
      if (repo.findByEmail(email).isEmpty()) {
        var u = new UsuarioEntity();
        u.email = email;
        u.passwordHash = encoder.encode(password);
        u.rol = "ADMIN";
        repo.save(u);
      }
    };
  }
}
