-- esquema del primer sprint
CREATE DATABASE IF NOT EXISTS barberia_testing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE barberia_testing;
CREATE TABLE IF NOT EXISTS usuarios (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 email VARCHAR(160) NOT NULL UNIQUE,
 password_hash VARCHAR(100) NOT NULL,
 rol VARCHAR(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS servicios (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(100) NOT NULL,
 nombre_clave VARCHAR(100) NOT NULL,
 precio DECIMAL(6,2) NOT NULL,
 duracion_minutos INT NOT NULL,
 CONSTRAINT uk_servicio_nombre UNIQUE (nombre_clave),
 CONSTRAINT ck_precio CHECK (precio >= 0),
 CONSTRAINT ck_duracion CHECK (duracion_minutos BETWEEN 15 AND 240 AND MOD(duracion_minutos,15)=0)
);

