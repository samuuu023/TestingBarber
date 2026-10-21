package sv.edu.esfe.barberia.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.esfe.barberia.application.port.out.ServicioRepositoryPort;
import sv.edu.esfe.barberia.application.usecase.ServicioService;
import sv.edu.esfe.barberia.domain.exception.*;
import sv.edu.esfe.barberia.domain.model.Servicio;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {
  @Mock ServicioRepositoryPort repo;
  @InjectMocks ServicioService service;

  private Servicio corte(Long id) {
    return new Servicio(id, "Corte", new BigDecimal("6.00"), 30);
  }

  @Test
  void crearCuandoNoExiste() {
    when(repo.guardar(any())).thenReturn(corte(1L));
    var s = service.crear(corte(null));
    assertEquals(1L, s.id());
    verify(repo).guardar(corte(null));
  }

  @Test
  void noCrearDuplicado() {
    when(repo.existeNombre("Corte", null)).thenReturn(true);
    assertThrows(DuplicadoException.class, () -> service.crear(corte(null)));
    verify(repo, never()).guardar(any());
  }

  @Test
  void ignorarIdClienteAlCrear() {
    service.crear(corte(50L));
    verify(repo).guardar(corte(null));
  }

  @Test
  void buscarExistente() {
    when(repo.buscar(1L)).thenReturn(Optional.of(corte(1L)));
    assertEquals("Corte", service.buscar(1L).nombre());
  }

  @Test
  void buscarAusente() {
    assertThrows(NoEncontradoException.class, () -> service.buscar(99L));
  }

  @Test
  void listar() {
    when(repo.listar()).thenReturn(List.of(corte(1L)));
    assertEquals(1, service.listar().size());
  }

  @Test
  void actualizarExistente() {
    when(repo.buscar(1L)).thenReturn(Optional.of(corte(1L)));
    when(repo.guardar(any())).thenReturn(corte(1L));
    assertEquals(1L, service.actualizar(1L, corte(null)).id());
    verify(repo).existeNombre("Corte", 1L);
  }

  @Test
  void actualizarAusenteNoGuarda() {
    assertThrows(NoEncontradoException.class, () -> service.actualizar(99L, corte(null)));
    verify(repo, never()).guardar(any());
  }

  @Test
  void actualizarNombreDuplicado() {
    when(repo.buscar(1L)).thenReturn(Optional.of(corte(1L)));
    when(repo.existeNombre("Corte", 1L)).thenReturn(true);
    assertThrows(DuplicadoException.class, () -> service.actualizar(1L, corte(null)));
    verify(repo, never()).guardar(any());
  }

  @Test
  void eliminarExistente() {
    when(repo.buscar(1L)).thenReturn(Optional.of(corte(1L)));
    service.eliminar(1L);
    verify(repo).eliminar(1L);
  }

  @Test
  void eliminarAusenteNoBorra() {
    assertThrows(NoEncontradoException.class, () -> service.eliminar(99L));
    verify(repo, never()).eliminar(any());
  }
}
