-- Datos de prueba de db_usuarios. INSERT IGNORE con ids fijos: no se duplican al reiniciar.
-- Contraseña de ambos usuarios: Oursour2026 (hash BCrypt, igual que BCryptPasswordEncoder)
INSERT IGNORE INTO usuario (id_usuario, nombre, email, password_hash, rol, fecha_registro) VALUES
    (1, 'Admin Our Sour', 'admin@oursour.cl',   '$2a$10$WddzXPYNhuw.1LGjiNpLPeqp.0CihysD5.5/wTCxJfXWlJQcIDIf.', 'ADMIN',      '2026-09-01 10:00:00'),
    (2, 'Cliente Prueba', 'cliente@oursour.cl', '$2a$10$WddzXPYNhuw.1LGjiNpLPeqp.0CihysD5.5/wTCxJfXWlJQcIDIf.', 'CONSUMIDOR', '2026-09-15 10:00:00');
