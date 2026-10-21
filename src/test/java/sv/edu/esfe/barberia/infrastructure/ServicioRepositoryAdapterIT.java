package sv.edu.esfe.barberia.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.esfe.barberia.domain.model.Servicio;
import sv.edu.esfe.barberia.infrastructure.persistence.adapter.ServicioRepositoryAdapter;

@DataJpaTest
@Import(ServicioRepositoryAdapter.class)
@ActiveProfiles("test")
class ServicioRepositoryAdapterIT {
  @Autowired ServicioRepositoryAdapter repo;

  @Test
  void guardarYRecuperar() {
    var s = repo.guardar(new Servicio(null, "Barba", new BigDecimal("4.00"), 15));
    assertNotNull(s.id());
    assertEquals(s, repo.buscar(s.id()).orElseThrow());
  }

  @Test
  void duplicadoIgnoraMayusculas() {
    repo.guardar(new Servicio(null, "Barba", BigDecimal.ONE, 15));
    assertTrue(repo.existeNombre("BARBA", null));
  }

  @Test
  void mismoIdNoEsDuplicado() {
    var s = repo.guardar(new Servicio(null, "Corte", BigDecimal.ONE, 30));
    assertFalse(repo.existeNombre("CORTE", s.id()));
  }

  @Test
  void restriccionUnicaEnBase() {
    repo.guardar(new Servicio(null, "Corte", BigDecimal.ONE, 30));
    assertThrows(
        org.springframework.dao.DataIntegrityViolationException.class,
        () -> repo.guardar(new Servicio(null, "CORTE", BigDecimal.TEN, 30)));
  }

  @Test
  void borrarRegistro() {
    var s = repo.guardar(new Servicio(null, "Lavado", BigDecimal.ONE, 15));
    repo.eliminar(s.id());
    assertTrue(repo.buscar(s.id()).isEmpty());
  }
}
