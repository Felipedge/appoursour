package cl.oursour.ms_usuarios.service;

import cl.oursour.ms_usuarios.dto.AuthResponse;
import cl.oursour.ms_usuarios.dto.LoginRequest;
import cl.oursour.ms_usuarios.dto.RegisterRequest;
import cl.oursour.ms_usuarios.model.Usuario;
import cl.oursour.ms_usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegisterRequest req) {
        String email = normalizarEmail(req.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email " + email + " ya está registrado");
        }

        Usuario usuario = new Usuario(
                req.nombre().trim(),
                email,
                passwordEncoder.encode(req.password()),
                Usuario.Rol.CONSUMIDOR,
                LocalDateTime.now());
        usuario = usuarioRepository.save(usuario);

        return new AuthResponse(jwtService.generarToken(usuario), usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(req.email()))
                .filter(u -> passwordEncoder.matches(req.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Email o contraseña incorrectos"));

        return new AuthResponse(jwtService.generarToken(usuario), usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse.DatosUsuario obtenerPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(AuthResponse.DatosUsuario::desde)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private static String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
