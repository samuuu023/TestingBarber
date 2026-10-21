package sv.edu.esfe.barberia.infrastructure.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sv.edu.esfe.barberia.application.usecase.LoginService;

@RestController
public class AuthController {
  private final LoginService login;
  private final long ttl;

  public AuthController(LoginService login, @Value("${app.jwt.ttl-seconds}") long ttl) {
    this.login = login;
    this.ttl = ttl;
  }

  public record LoginRequest(
      @NotBlank @Email @Size(max = 160) String email, @NotBlank @Size(max = 100) String password) {}

  public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}

  @PostMapping("/api/auth/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest r) {
    return new TokenResponse(login.ejecutar(r.email(), r.password()), "Bearer", ttl);
  }

  @GetMapping("/api/auth/me")
  public Map<String, String> me(Authentication a) {
    return Map.of("email", a.getName());
  }

  @GetMapping("/api/salud")
  public Map<String, String> salud() {
    return Map.of("estado", "OK", "aplicacion", "Cool Barber Shop");
  }
}
