-- Datos de prueba de db_recetas. INSERT IGNORE con ids fijos: no se duplican al reiniciar.
-- id_producto debe existir en ms_catalogo.

INSERT IGNORE INTO ingrediente (id_ingrediente, nombre, categoria, id_producto) VALUES
    (1, 'Pisco',              'DESTILADO', NULL),
    (2, 'Jugo de limón',      'JUGO',      NULL),
    (3, 'Azúcar flor',        'OTRO',      NULL),
    (4, 'Hielo',              'OTRO',      NULL),
    (5, 'Our Sour Clásico',   'OUR_SOUR',  1),
    (6, 'Maracuyá',           'FRUTA',     NULL),
    (7, 'Agua tónica',        'MIXER',     NULL);

INSERT IGNORE INTO receta (id_receta, titulo, tipo, pasos_preparacion, id_producto) VALUES
    (1, 'Pisco Sour tradicional', 'SOUR',
        '1. Agregar pisco, jugo de limón y azúcar a la coctelera.\n2. Añadir hielo y batir 15 segundos.\n3. Colar y servir en copa.',
        NULL),
    (2, 'Sour de maracuyá con Our Sour', 'SOUR',
        '1. Mezclar Our Sour Clásico con pulpa de maracuyá.\n2. Batir con hielo.\n3. Servir frío.',
        1);

INSERT IGNORE INTO receta_ingrediente (id_detalle, id_receta, id_ingrediente, dosificacion) VALUES
    (1, 1, 1, '90 ml'),
    (2, 1, 2, '30 ml'),
    (3, 1, 3, '2 cucharadas'),
    (4, 1, 4, 'a gusto'),
    (5, 2, 5, '120 ml'),
    (6, 2, 6, '40 ml'),
    (7, 2, 4, 'a gusto');
