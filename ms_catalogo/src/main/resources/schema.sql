-- Esquema de db_catalogo (ms_catalogo)
-- Se ejecuta en cada arranque (spring.sql.init.mode=always), por eso usa IF NOT EXISTS.

CREATE TABLE IF NOT EXISTS producto (
    id_producto       BIGINT          NOT NULL AUTO_INCREMENT,
    nombre_comercial  VARCHAR(150)    NOT NULL,
    descripcion       VARCHAR(500),
    precio_unitario   DECIMAL(10, 2)  NOT NULL,
    stock_simulado    INT             NOT NULL,
    -- false = borrado lógico
    activo            BOOLEAN         NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_producto),
    CONSTRAINT chk_producto_precio CHECK (precio_unitario >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
