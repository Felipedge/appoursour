package cl.oursour.ms_usuarios.controller;

import cl.oursour.ms_usuarios.dto.AuthResponse;
import cl.oursour.ms_usuarios.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final AuthService authService;

    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.DatosUsuario> me(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(authService.obtenerPorId(userId));
    }
}
