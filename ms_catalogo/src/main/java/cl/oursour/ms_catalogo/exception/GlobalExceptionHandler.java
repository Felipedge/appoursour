package cl.oursour.ms_catalogo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String mensaje,
            Map<String, String> campos
    ) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", campos);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> validacionParametros(HandlerMethodValidationException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(r -> r.getResolvableErrors().forEach(e ->
                campos.putIfAbsent(r.getMethodParameter().getParameterName(), e.getDefaultMessage())));
        return construir(HttpStatus.BAD_REQUEST, "Parámetros inválidos", campos);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> sinUsuario(MissingRequestHeaderException ex) {
        return construir(HttpStatus.UNAUTHORIZED,
                "Falta la identificación del usuario: accede a través del gateway con un token válido", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        return construir(HttpStatus.BAD_REQUEST, "Valor inválido para '" + ex.getName() + "': " + ex.getValue(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> negocio(ResponseStatusException ex) {
        return construir(ex.getStatusCode(), ex.getReason(), null);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
        return construir(HttpStatus.CONFLICT, "La operación entra en conflicto con datos existentes", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(NoResourceFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, "Ruta no encontrada", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatusCode status, String mensaje, Map<String, String> campos) {
        HttpStatus estado = HttpStatus.resolve(status.value());
        String error = estado != null ? estado.getReasonPhrase() : String.valueOf(status.value());
        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), status.value(), error, mensaje, campos);
        return ResponseEntity.status(status).body(body);
    }
}
