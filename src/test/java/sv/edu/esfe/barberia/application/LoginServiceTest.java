package sv.edu.esfe.barberia.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.esfe.barberia.application.port.out.*;
import sv.edu.esfe.barberia.application.usecase.LoginService;
import sv.edu.esfe.barberia.domain.exception.CredencialesException;
import sv.edu.esfe.barberia.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
  @Mock UsuarioRepositoryPort usuarios;
  @Mock PasswordPort passwords;
  @Mock TokenPort tokens;
  @InjectMocks LoginService login;
  private Usuario admin = new Usuario(1L, "admin@barberia.com", "hash", "ADMIN");

  @Test
  void loginCorrectoNormalizaEmail() {
    when(usuarios.buscarPorEmail(admin.email())).thenReturn(Optional.of(admin));
    when(passwords.coincide("clave", "hash")).thenReturn(true);
    when(tokens.generar(admin)).thenReturn("jwt");
    assertEquals("jwt", login.ejecutar(" ADMIN@BARBERIA.COM ", "clave"));
  }

  @Test
  void usuarioDesconocido() {
    assertThrows(CredencialesException.class, () -> login.ejecutar("x@y.com", "clave"));
    verifyNoInteractions(tokens, passwords);
  }

  @Test
  void passwordIncorrectaNoEmiteToken() {
    when(usuarios.buscarPorEmail(admin.email())).thenReturn(Optional.of(admin));
    assertThrows(CredencialesException.class, () -> login.ejecutar(admin.email(), "mala"));
    verifyNoInteractions(tokens);
  }

  @Test
  void emailNulo() {
    assertThrows(CredencialesException.class, () -> login.ejecutar(null, "x"));
    verifyNoInteractions(usuarios, tokens, passwords);
  }

  @Test
  void passwordNula() {
    assertThrows(CredencialesException.class, () -> login.ejecutar("a@b.com", null));
    verifyNoInteractions(usuarios, tokens, passwords);
  }
}
