-- Esquema de db_pago (ms_pago)
-- Se ejecuta en cada arranque (spring.sql.init.mode=always), por eso usa IF NOT EXISTS.
-- id_pedido apunta a ms_pedido (otra base): no lleva FOREIGN KEY, se valida por HTTP.

CREATE TABLE IF NOT EXISTS pago (
    id_pago     BIGINT       NOT NULL AUTO_INCREMENT,
    id_pedido   BIGINT       NOT NULL,
    monto       INT          NOT NULL,
    metodo      VARCHAR(30)  NOT NULL,
    estado      VARCHAR(30)  NOT NULL DEFAULT 'APROBADO',
    fecha_pago  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_pago),
    INDEX idx_pago_pedido (id_pedido),
    CONSTRAINT chk_pago_monto CHECK (monto >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
