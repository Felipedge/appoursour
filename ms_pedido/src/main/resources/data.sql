-- Datos de prueba de db_pedido. INSERT IGNORE con ids fijos: no se duplican al reiniciar.
-- id_usuario e id_producto deben existir en ms-usuarios y ms_catalogo respectivamente.

INSERT IGNORE INTO direccion_despacho (id_direccion, id_usuario, calle, numero, comuna, ciudad, referencia) VALUES
    (1, 1, 'Av. Providencia', '1234', 'Providencia', 'Santiago', 'Depto 501'),
    (2, 1, 'Los Aromos',      '56',   'Ñuñoa',       'Santiago', NULL),
    (3, 2, 'Av. Libertad',    '890',  'Viña del Mar','Valparaíso','Casa esquina');

INSERT IGNORE INTO pedido (id_pedido, id_usuario, id_direccion, subtotal, costo_despacho, monto_total, estado, fecha_creacion) VALUES
    (1, 1, 1, 14990, 2990, 17980, 'SOLICITADO',     '2026-10-01 18:25:00'),
    (2, 2, 3,  6990, 3000,  9990, 'PAGO_RECHAZADO', '2026-10-02 20:10:00');

INSERT IGNORE INTO pedido_producto (id_detalle, id_pedido, id_producto, cantidad, precio_unitario) VALUES
    (1, 1, 1, 1, 7990),
    (2, 1, 2, 1, 7000),
    (3, 2, 3, 1, 6990);

INSERT IGNORE INTO carrito (id_carrito, id_usuario, fecha_creacion) VALUES
    (1, 1, '2026-10-05 12:00:00');

INSERT IGNORE INTO carrito_producto (id_detalle, id_carrito, id_producto, cantidad) VALUES
    (1, 1, 1, 2),
    (2, 1, 3, 1);
