CREATE DATABASE IF NOT EXISTS taller_reparacion
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE taller_reparacion;

CREATE TABLE vehiculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricula VARCHAR(20) NOT NULL UNIQUE,
    modelo VARCHAR(100) NOT NULL,
    fecha_matriculacion DATE
) ENGINE=InnoDB;

CREATE TABLE averias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME(6) NOT NULL,
    fecha_ultima_actualizacion DATETIME(6) NOT NULL,
    vehiculo_id BIGINT NOT NULL,

    CONSTRAINT chk_estado
        CHECK (estado IN (
            'PENDIENTE',
            'EN_REPARACION',
            'TERMINADO'
        )),

    CONSTRAINT fk_averias_vehiculo
        FOREIGN KEY (vehiculo_id)
        REFERENCES vehiculos(id)
        ON DELETE RESTRICT
);