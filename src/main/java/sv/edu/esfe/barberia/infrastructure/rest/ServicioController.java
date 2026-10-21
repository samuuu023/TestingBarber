package sv.edu.esfe.barberia.infrastructure.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sv.edu.esfe.barberia.application.port.in.ServicioUseCase;
import sv.edu.esfe.barberia.domain.model.Servicio;

@RestController
@RequestMapping("/api/servicios")
@Validated
public class ServicioController {
  private final ServicioUseCase useCase;

  public ServicioController(ServicioUseCase useCase) {
    this.useCase = useCase;
  }

  @PostMapping
  public ResponseEntity<Servicio> crear(@Valid @RequestBody ServicioRequest r) {
    var s = useCase.crear(r.toDomain());
    return ResponseEntity.created(URI.create("/api/servicios/" + s.id())).body(s);
  }

  @GetMapping
  public List<Servicio> listar() {
    return useCase.listar();
  }

  @GetMapping("/{id}")
  public Servicio buscar(@PathVariable @Positive Long id) {
    return useCase.buscar(id);
  }

  @PutMapping("/{id}")
  public Servicio actualizar(
      @PathVariable @Positive Long id, @Valid @RequestBody ServicioRequest r) {
    return useCase.actualizar(id, r.toDomain());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
    useCase.eliminar(id);
    return ResponseEntity.noContent().build();
  }
}
