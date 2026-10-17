-- Esquema de db_metricas (ms_metricas)
-- Se ejecuta en cada arranque (spring.sql.init.mode=always), por eso usa IF NOT EXISTS.
-- id_usuario, id_ingrediente e id_receta viven en otros servicios: sin FOREIGN KEY, solo índice.

CREATE TABLE IF NOT EXISTS busqueda (
    id_busqueda     BIGINT       NOT NULL AUTO_INCREMENT,
    id_usuario      BIGINT       NOT NULL,
    fecha_busqueda  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id_busqueda),
    INDEX idx_busqueda_fecha (fecha_busqueda)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS busqueda_ingrediente (
    id_busqueda     BIGINT  NOT NULL,
    id_ingrediente  BIGINT  NOT NULL,
    PRIMARY KEY (id_busqueda, id_ingrediente),
    INDEX idx_busqueda_ingrediente_ingrediente (id_ingrediente),
    CONSTRAINT fk_busqueda_ingrediente_busqueda FOREIGN KEY (id_busqueda)
        REFERENCES busqueda (id_busqueda) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS consulta_receta (
    id_consulta     BIGINT       NOT NULL AUTO_INCREMENT,
    id_usuario      BIGINT       NOT NULL,
    id_receta       BIGINT       NOT NULL,
    fecha_consulta  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id_consulta),
    INDEX idx_consulta_receta_receta (id_receta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
