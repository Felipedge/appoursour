-- Datos de prueba de db_metricas. INSERT IGNORE con ids fijos: no se duplican al reiniciar.
-- id_ingrediente e id_receta corresponden a los datos de prueba de ms_recetas.

INSERT IGNORE INTO busqueda (id_busqueda, id_usuario, fecha_busqueda) VALUES
    (1, 2, '2026-10-10 19:00:00'),
    (2, 2, '2026-10-12 21:30:00'),
    (3, 1, '2026-10-15 18:45:00');

INSERT IGNORE INTO busqueda_ingrediente (id_busqueda, id_ingrediente) VALUES
    (1, 1), (1, 2),
    (2, 1), (2, 6),
    (3, 1), (3, 2), (3, 5);

INSERT IGNORE INTO consulta_receta (id_consulta, id_usuario, id_receta, fecha_consulta) VALUES
    (1, 2, 1, '2026-10-10 19:05:00'),
    (2, 2, 2, '2026-10-12 21:35:00'),
    (3, 1, 1, '2026-10-15 18:50:00');
