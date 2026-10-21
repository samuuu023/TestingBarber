package sv.edu.esfe.barberia.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sv.edu.esfe.barberia.infrastructure.persistence.repository.ServicioJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;
  @Autowired ServicioJpaRepository repo;
  @Autowired JwtEncoder encoder;
  String token;
  String body = "{\"nombre\":\"Corte\",\"precio\":6.00,\"duracionMinutos\":30}";

  @BeforeEach
  void prepare() throws Exception {
    repo.deleteAll();
    token = login();
  }

  String login() throws Exception {
    var result =
        mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"admin@barberia.com\",\"password\":\"Admin123!\"}"))
            .andExpect(status().isOk())
            .andReturn();
    return json.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
  }

  String bearer() {
    return "Bearer " + token;
  }

  @Test
  void humoSalud() throws Exception {
    mvc.perform(get("/api/salud"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estado").value("OK"));
  }

  @Test
  void aceptarCrudCompleto() throws Exception {
    var response =
        mvc.perform(
                post("/api/servicios")
                    .header("Authorization", bearer())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn();
    long id = json.readTree(response.getResponse().getContentAsString()).get("id").asLong();
    mvc.perform(get("/api/servicios/" + id).header("Authorization", bearer()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Corte"));
    mvc.perform(get("/api/servicios").header("Authorization", bearer()))
        .andExpect(jsonPath("$.length()").value(1));
    mvc.perform(
            put("/api/servicios/" + id)
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("6.00", "8.50")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.precio").value(8.5));
    mvc.perform(delete("/api/servicios/" + id).header("Authorization", bearer()))
        .andExpect(status().isNoContent());
    mvc.perform(get("/api/servicios/" + id).header("Authorization", bearer()))
        .andExpect(status().isNotFound());
  }

  @Test
  void rechazarSinJwt() throws Exception {
    mvc.perform(get("/api/servicios")).andExpect(status().isUnauthorized());
  }

  @Test
  void rechazarJwtManipulado() throws Exception {
    String[] parts = token.split("\\.");
    parts[1] = "eyJzdWIiOiJhdGFjYW50ZSJ9";
    mvc.perform(get("/api/servicios").header("Authorization", "Bearer " + String.join(".", parts)))
        .andExpect(status().isUnauthorized());
  }

  String signed(String issuer, String audience, String role, Instant expiry) {
    var claims =
        JwtClaimsSet.builder()
            .issuer(issuer)
            .subject("test@barberia.com")
            .audience(List.of(audience))
            .issuedAt(expiry.minusSeconds(3600))
            .expiresAt(expiry)
            .claim("roles", List.of(role))
            .build();
    return encoder
        .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
        .getTokenValue();
  }

  @Test
  void rechazarJwtVencido() throws Exception {
    mvc.perform(
            get("/api/servicios")
                .header(
                    "Authorization",
                    "Bearer "
                        + signed(
                            "barberia-testing",
                            "barberia-api",
                            "ADMIN",
                            Instant.now().minusSeconds(3600))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void rechazarOtroEmisor() throws Exception {
    mvc.perform(
            get("/api/servicios")
                .header(
                    "Authorization",
                    "Bearer "
                        + signed("otro", "barberia-api", "ADMIN", Instant.now().plusSeconds(3600))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void rechazarOtraAudiencia() throws Exception {
    mvc.perform(
            get("/api/servicios")
                .header(
                    "Authorization",
                    "Bearer "
                        + signed(
                            "barberia-testing",
                            "otra-api",
                            "ADMIN",
                            Instant.now().plusSeconds(3600))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void rechazarRolUsuario() throws Exception {
    mvc.perform(
            get("/api/servicios")
                .header(
                    "Authorization",
                    "Bearer "
                        + signed(
                            "barberia-testing",
                            "barberia-api",
                            "USER",
                            Instant.now().plusSeconds(3600))))
        .andExpect(status().isForbidden());
  }

  @Test
  void loginIncorrecto() throws Exception {
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@barberia.com\",\"password\":\"incorrecta\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value("Credenciales incorrectas"));
  }

  @Test
  void loginEmailInvalido() throws Exception {
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"invalido\",\"password\":\"x\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void meNoExponeHash() throws Exception {
    mvc.perform(get("/api/auth/me").header("Authorization", bearer()))
        .andExpect(jsonPath("$.email").value("admin@barberia.com"))
        .andExpect(jsonPath("$.passwordHash").doesNotExist());
  }

  @Test
  void rechazarDuplicado() throws Exception {
    mvc.perform(
            post("/api/servicios")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated());
    mvc.perform(
            post("/api/servicios")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("Corte", " CORTE ")))
        .andExpect(status().isConflict());
  }

  @Test
  void rechazarNombreVacio() throws Exception {
    mvc.perform(
            post("/api/servicios")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("Corte", " ")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rechazarPrecioNegativo() throws Exception {
    mvc.perform(
            post("/api/servicios")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("6.00", "-1")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rechazarDuracionNoMultiplo() throws Exception {
    mvc.perform(
            post("/api/servicios")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace(":30", ":16")))
        .andExpect(status().isBadRequest());
  }

  @Test
  void rechazarJsonRoto() throws Exception {
    mvc.perform(
            post("/api/servicios")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void actualizarAusente() throws Exception {
    mvc.perform(
            put("/api/servicios/999999")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  void borrarAusente() throws Exception {
    mvc.perform(delete("/api/servicios/999999").header("Authorization", bearer()))
        .andExpect(status().isNotFound());
  }

  @Test
  void escrituraSinJwtNoModifica() throws Exception {
    mvc.perform(post("/api/servicios").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isUnauthorized());
    assertEquals(0, repo.count());
  }
}
