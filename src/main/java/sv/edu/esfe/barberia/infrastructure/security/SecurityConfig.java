package sv.edu.esfe.barberia.infrastructure.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String secret) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(key(secret)));
  }

  @Bean
  JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
    var decoder =
        NimbusJwtDecoder.withSecretKey(key(secret)).macAlgorithm(MacAlgorithm.HS256).build();
    var audience =
        new JwtClaimValidator<java.util.List<String>>(
            "aud", a -> a != null && a.contains("barberia-api"));
    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer("barberia-testing"), audience));
    return decoder;
  }

  private SecretKeySpec key(String secret) {
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32)
      throw new IllegalArgumentException("JWT_SECRET requiere al menos 32 bytes");
    return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
    var authorities = new JwtGrantedAuthoritiesConverter();
    authorities.setAuthoritiesClaimName("roles");
    authorities.setAuthorityPrefix("ROLE_");
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(authorities);
    return http.csrf(c -> c.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/", "/index.html", "/api/salud", "/api/auth/login")
                    .permitAll()
                    .anyRequest()
                    .hasRole("ADMIN"))
        .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)))
        .build();
  }
}
