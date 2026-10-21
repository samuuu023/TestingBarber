package sv.edu.esfe.barberia.infrastructure.security;

import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import sv.edu.esfe.barberia.application.port.out.TokenPort;
import sv.edu.esfe.barberia.domain.model.Usuario;

@Component
public class JwtTokenAdapter implements TokenPort {
  private final JwtEncoder encoder;
  private final long ttl;

  public JwtTokenAdapter(JwtEncoder encoder, @Value("${app.jwt.ttl-seconds}") long ttl) {
    this.encoder = encoder;
    this.ttl = ttl;
  }

  public String generar(Usuario u) {
    var now = Instant.now();
    var claims =
        JwtClaimsSet.builder()
            .issuer("barberia-testing")
            .subject(u.email())
            .audience(List.of("barberia-api"))
            .issuedAt(now)
            .expiresAt(now.plusSeconds(ttl))
            .claim("roles", List.of(u.rol()))
            .build();
    return encoder
        .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
        .getTokenValue();
  }
}
