-- YachayKard - Módulo 2: Estructura Académica y Matrícula
-- Tablas: grado, curso, apoderado, estudiante, seccion, matricula
-- Ejecutar después de 01_modulo1.sql

USE yachaykard_db;

SET
FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS matricula;
DROP TABLE IF EXISTS seccion;
DROP TABLE IF EXISTS estudiante;
DROP TABLE IF EXISTS apoderado;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS grado;
SET
FOREIGN_KEY_CHECKS = 1;

CREATE TABLE grado
(
    id_grado     INT         NOT NULL AUTO_INCREMENT,
    numero_grado VARCHAR(20) NOT NULL,
    nivel        VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_grado)
) ENGINE=InnoDB;

CREATE TABLE curso
(
    id_curso        INT          NOT NULL AUTO_INCREMENT,
    nombre_curso    VARCHAR(100) NOT NULL,
    horas_semanales INT,
    codigo_siagie   VARCHAR(20),
    PRIMARY KEY (id_curso)
) ENGINE=InnoDB;

CREATE TABLE apoderado
(
    id_apoderado INT          NOT NULL AUTO_INCREMENT,
    dni          VARCHAR(8)   NOT NULL,
    nombres      VARCHAR(100) NOT NULL,
    telefono     VARCHAR(15),
    PRIMARY KEY (id_apoderado)
) ENGINE=InnoDB;

CREATE TABLE estudiante
(
    id_estudiante     INT          NOT NULL AUTO_INCREMENT,
    dni               VARCHAR(8)   NOT NULL,
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    fecha_nacimiento  DATE,
    id_apoderado      INT          NOT NULL,
    id_siagie         VARCHAR(20),
    codigo_estudiante CHAR(14),
    PRIMARY KEY (id_estudiante),
    CONSTRAINT fk_estudiante_apoderado FOREIGN KEY (id_apoderado) REFERENCES apoderado (id_apoderado)
) ENGINE=InnoDB;
CREATE TABLE seccion
(
    id_seccion       INT     NOT NULL AUTO_INCREMENT,
    letra            CHAR(1) NOT NULL,
    aforo            INT,
    id_grado         INT     NOT NULL,
    id_docente_tutor INT NULL,
    id_auxiliar      INT NULL,
    PRIMARY KEY (id_seccion),
    CONSTRAINT fk_seccion_grado FOREIGN KEY (id_grado) REFERENCES grado (id_grado),
    CONSTRAINT fk_seccion_auxiliar FOREIGN KEY (id_auxiliar) REFERENCES auxiliar (id_auxiliar)
) ENGINE=InnoDB;
-- La FK de id_docente_tutor se agrega en 03_modulo3.sql (la tabla docente se crea ahí)

CREATE TABLE matricula
(
    id_matricula    INT         NOT NULL AUTO_INCREMENT,
    fecha_matricula DATE        NOT NULL,
    anio_lectivo    VARCHAR(10) NOT NULL,
    estado          VARCHAR(15) NOT NULL,
    id_estudiante   INT         NOT NULL,
    id_seccion      INT         NOT NULL,
    PRIMARY KEY (id_matricula),
    CONSTRAINT fk_matricula_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
    CONSTRAINT fk_matricula_seccion FOREIGN KEY (id_seccion) REFERENCES seccion (id_seccion)
) ENGINE=InnoDB;