-- Datos de prueba de db_catalogo. INSERT IGNORE con ids fijos: no se duplican al reiniciar.
-- Los ids 1, 2 y 3 son los que usan los datos de prueba de ms_pedido y ms_recetas.
INSERT IGNORE INTO producto (id_producto, nombre_comercial, descripcion, precio_unitario, stock_simulado, activo) VALUES
    (1, 'Our Sour Clásico 700 ml',  'Mezcla lista para pisco sour tradicional', 7990.00, 50, TRUE),
    (2, 'Our Sour Maracuyá 700 ml', 'Mezcla para sour sabor maracuyá',          7000.00, 40, TRUE),
    (3, 'Our Sour Mango 700 ml',    'Mezcla para sour sabor mango',             6990.00, 30, TRUE);
