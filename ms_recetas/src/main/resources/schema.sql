-- Esquema de db_recetas (ms_recetas)
-- Se ejecuta en cada arranque (spring.sql.init.mode=always), por eso usa IF NOT EXISTS.
-- id_producto apunta a ms_catalogo (otra base): no lleva FOREIGN KEY, solo índice.

CREATE TABLE IF NOT EXISTS ingrediente (
    id_ingrediente  BIGINT        NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)  NOT NULL,
    -- DESTILADO, LICOR, FRUTA, JUGO, MIXER, OUR_SOUR, OTRO
    categoria       VARCHAR(30)   NOT NULL,
    -- Solo para ingredientes OUR_SOUR
    id_producto     BIGINT,
    PRIMARY KEY (id_ingrediente),
    UNIQUE KEY uk_ingrediente_nombre (nombre),
    INDEX idx_ingrediente_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS receta (
    id_receta          BIGINT        NOT NULL AUTO_INCREMENT,
    titulo             VARCHAR(150)  NOT NULL,
    tipo               VARCHAR(50),
    pasos_preparacion  TEXT,
    id_producto        BIGINT,
    PRIMARY KEY (id_receta),
    INDEX idx_receta_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS receta_ingrediente (
    id_detalle      BIGINT       NOT NULL AUTO_INCREMENT,
    id_receta       BIGINT       NOT NULL,
    id_ingrediente  BIGINT       NOT NULL,
    dosificacion    VARCHAR(50),
    PRIMARY KEY (id_detalle),
    CONSTRAINT fk_receta_ingrediente_receta FOREIGN KEY (id_receta)
        REFERENCES receta (id_receta) ON DELETE CASCADE,
    CONSTRAINT fk_receta_ingrediente_ingrediente FOREIGN KEY (id_ingrediente)
        REFERENCES ingrediente (id_ingrediente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
