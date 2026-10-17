-- Datos de prueba de db_pago. INSERT IGNORE con ids fijos: no se duplican al reiniciar.
INSERT IGNORE INTO pago (id_pago, id_pedido, monto, metodo, estado, fecha_pago) VALUES
    (1, 1, 17980, 'DEBITO',  'APROBADO',  '2026-10-01 18:30:00'),
    (2, 2,  9990, 'CREDITO', 'RECHAZADO', '2026-10-02 20:15:00');
