package sv.edu.esfe.barberia.infrastructure.config;

import org.springframework.context.annotation.*;
import sv.edu.esfe.barberia.application.port.in.ServicioUseCase;
import sv.edu.esfe.barberia.application.port.out.*;
import sv.edu.esfe.barberia.application.usecase.*;

@Configuration
public class ApplicationConfig {
  @Bean
  ServicioUseCase servicios(ServicioRepositoryPort repo) {
    return new ServicioService(repo);
  }

  @Bean
  LoginService login(UsuarioRepositoryPort u, PasswordPort p, TokenPort t) {
    return new LoginService(u, p, t);
  }
}
