-- YachayKard - Módulo 1: Seguridad e Institución
-- Tablas: institucion, rol, permiso, rol_permiso, usuario, sesion, auxiliar

CREATE DATABASE IF NOT EXISTS yachaykard_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE yachaykard_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS auxiliar;
DROP TABLE IF EXISTS sesion;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol_permiso;
DROP TABLE IF EXISTS permiso;
DROP TABLE IF EXISTS rol;
DROP TABLE IF EXISTS institucion;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE institucion (
    id_institucion     INT NOT NULL AUTO_INCREMENT,
    nombre             VARCHAR(150) NOT NULL,
    codigo_modular     VARCHAR(20)  NOT NULL,
    nivel              VARCHAR(20)  NOT NULL,
    nombre_director    VARCHAR(150),
    direccion          VARCHAR(200),
    departamento       VARCHAR(50),
    provincia          VARCHAR(50),
    distrito           VARCHAR(50),
    telefono           VARCHAR(15),
    correo_electronico VARCHAR(100),
    PRIMARY KEY (id_institucion)
) ENGINE=InnoDB;

CREATE TABLE rol (
    id_rol     INT NOT NULL AUTO_INCREMENT,
    nombre_rol VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_rol)
) ENGINE=InnoDB;

CREATE TABLE permiso (
    id_permiso     INT NOT NULL AUTO_INCREMENT,
    nombre_permiso VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_permiso)
) ENGINE=InnoDB;

CREATE TABLE rol_permiso (
    id_rol     INT NOT NULL,
    id_permiso INT NOT NULL,
    PRIMARY KEY (id_rol, id_permiso),
    CONSTRAINT fk_rolpermiso_rol     FOREIGN KEY (id_rol)     REFERENCES rol (id_rol),
    CONSTRAINT fk_rolpermiso_permiso FOREIGN KEY (id_permiso) REFERENCES permiso (id_permiso)
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id_usuario     INT NOT NULL AUTO_INCREMENT,
    username       VARCHAR(50)  NOT NULL,
    password       VARCHAR(255) NOT NULL,
    id_rol         INT NOT NULL,
    id_institucion INT NOT NULL,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuario_username (username),
    CONSTRAINT fk_usuario_rol         FOREIGN KEY (id_rol)         REFERENCES rol (id_rol),
    CONSTRAINT fk_usuario_institucion FOREIGN KEY (id_institucion) REFERENCES institucion (id_institucion)
) ENGINE=InnoDB;

CREATE TABLE sesion (
    id_sesion    INT NOT NULL AUTO_INCREMENT,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin    DATETIME NULL,
    id_usuario   INT NOT NULL,
    PRIMARY KEY (id_sesion),
    CONSTRAINT fk_sesion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE auxiliar (
    id_auxiliar INT NOT NULL AUTO_INCREMENT,
    dni         VARCHAR(8)   NOT NULL,
    nombres     VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100) NOT NULL,
    id_usuario  INT NOT NULL,
    PRIMARY KEY (id_auxiliar),
    UNIQUE KEY uk_auxiliar_usuario (id_usuario),
    CONSTRAINT fk_auxiliar_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

-- Roles que pueden iniciar sesión
INSERT INTO rol (nombre_rol) VALUES ('Administrador'), ('Docente'), ('Auxiliar');