-- Esquema de db_usuarios (ms-usuarios)
-- Se ejecuta en cada arranque (spring.sql.init.mode=always), por eso usa IF NOT EXISTS.

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      BIGINT        NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)  NOT NULL,
    email           VARCHAR(150)  NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    -- ADMIN o CONSUMIDOR (enum Usuario.Rol)
    rol             VARCHAR(20)   NOT NULL,
    fecha_registro  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
