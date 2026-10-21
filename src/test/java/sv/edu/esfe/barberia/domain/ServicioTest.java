package sv.edu.esfe.barberia.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import sv.edu.esfe.barberia.domain.exception.ReglaNegocioException;
import sv.edu.esfe.barberia.domain.model.Servicio;

class ServicioTest {
  @Test
  @DisplayName("Crear servicio válido y normalizar espacios")
  void crearValido() {
    var s = new Servicio(1L, " Corte ", new BigDecimal("6.00"), 30);
    assertEquals("Corte", s.nombre());
    assertEquals(1L, s.id());
    assertEquals(new BigDecimal("6.00"), s.precio());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void rechazarNombreVacio(String nombre) {
    assertThrows(ReglaNegocioException.class, () -> new Servicio(null, nombre, BigDecimal.ONE, 30));
  }

  @Test
  void rechazarNombreLargo() {
    assertThrows(
        ReglaNegocioException.class, () -> new Servicio(null, "x".repeat(101), BigDecimal.ONE, 30));
  }

  @Test
  void rechazarPrecioNulo() {
    assertThrows(ReglaNegocioException.class, () -> new Servicio(null, "Corte", null, 30));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "10000", "2.555"})
  void rechazarPrecioInvalido(String precio) {
    assertThrows(
        ReglaNegocioException.class, () -> new Servicio(null, "Corte", new BigDecimal(precio), 30));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "9999.99", "1.2300"})
  void aceptarPrecioLimite(String precio) {
    assertDoesNotThrow(() -> new Servicio(null, "Corte", new BigDecimal(precio), 30));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 14, 16, 241, 300})
  void rechazarDuracion(int minutos) {
    assertThrows(
        ReglaNegocioException.class, () -> new Servicio(null, "Corte", BigDecimal.ONE, minutos));
  }

  @ParameterizedTest
  @ValueSource(ints = {15, 30, 240})
  void aceptarDuracion(int minutos) {
    assertEquals(minutos, new Servicio(null, "Corte", BigDecimal.ONE, minutos).duracionMinutos());
  }
}
