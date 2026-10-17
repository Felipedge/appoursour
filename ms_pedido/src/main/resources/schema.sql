-- Esquema de db_pedido (ms_pedido)
-- Se ejecuta en cada arranque (spring.sql.init.mode=always), por eso usa IF NOT EXISTS.
-- id_usuario (ms-usuarios) e id_producto (ms_catalogo) viven en otras bases:
-- no llevan FOREIGN KEY, solo índice. Las FK son únicamente entre tablas de este servicio.

CREATE TABLE IF NOT EXISTS direccion_despacho (
    id_direccion  BIGINT        NOT NULL AUTO_INCREMENT,
    id_usuario    BIGINT        NOT NULL,
    calle         VARCHAR(120)  NOT NULL,
    numero        VARCHAR(20)   NOT NULL,
    comuna        VARCHAR(80)   NOT NULL,
    ciudad        VARCHAR(80)   NOT NULL,
    referencia    VARCHAR(255),
    PRIMARY KEY (id_direccion),
    INDEX idx_direccion_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pedido (
    id_pedido       BIGINT       NOT NULL AUTO_INCREMENT,
    id_usuario      BIGINT       NOT NULL,
    id_direccion    BIGINT       NOT NULL,
    subtotal        INT          NOT NULL DEFAULT 0,
    costo_despacho  INT          NOT NULL DEFAULT 0,
    monto_total     INT          NOT NULL DEFAULT 0,
    -- PENDIENTE_PAGO, SOLICITADO, EN_PREPARACION o PAGO_RECHAZADO
    estado          VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE_PAGO',
    fecha_creacion  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_pedido),
    INDEX idx_pedido_usuario (id_usuario),
    INDEX idx_pedido_estado (estado),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (id_direccion)
        REFERENCES direccion_despacho (id_direccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS pedido_producto (
    id_detalle       BIGINT  NOT NULL AUTO_INCREMENT,
    id_pedido        BIGINT  NOT NULL,
    id_producto      BIGINT  NOT NULL,
    cantidad         INT     NOT NULL,
    precio_unitario  INT     NOT NULL,
    PRIMARY KEY (id_detalle),
    INDEX idx_pedido_producto_producto (id_producto),
    CONSTRAINT fk_pedido_producto_pedido FOREIGN KEY (id_pedido)
        REFERENCES pedido (id_pedido) ON DELETE CASCADE,
    CONSTRAINT chk_pedido_producto_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS carrito (
    id_carrito      BIGINT       NOT NULL AUTO_INCREMENT,
    id_usuario      BIGINT       NOT NULL,
    fecha_creacion  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_carrito),
    INDEX idx_carrito_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS carrito_producto (
    id_detalle   BIGINT  NOT NULL AUTO_INCREMENT,
    id_carrito   BIGINT  NOT NULL,
    id_producto  BIGINT  NOT NULL,
    cantidad     INT     NOT NULL,
    PRIMARY KEY (id_detalle),
    INDEX idx_carrito_producto_producto (id_producto),
    CONSTRAINT fk_carrito_producto_carrito FOREIGN KEY (id_carrito)
        REFERENCES carrito (id_carrito) ON DELETE CASCADE,
    CONSTRAINT chk_carrito_producto_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
