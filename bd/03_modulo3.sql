-- YachayKard - Módulo 3: Evaluación y Competencias
-- Tablas: docente, periodo, competencia, asignacion_curso, calificacion
-- Ejecutar después de 02_modulo2.sql

USE
yachaykard_db;

SET
FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS calificacion;
DROP TABLE IF EXISTS asignacion_curso;
DROP TABLE IF EXISTS competencia;
DROP TABLE IF EXISTS periodo;
DROP TABLE IF EXISTS docente;
SET
FOREIGN_KEY_CHECKS = 1;

CREATE TABLE docente
(
    id_docente   INT          NOT NULL AUTO_INCREMENT,
    dni          VARCHAR(8)   NOT NULL,
    nombres      VARCHAR(100) NOT NULL,
    apellidos    VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100),
    id_usuario   INT          NOT NULL,
    PRIMARY KEY (id_docente),
    UNIQUE KEY uk_docente_usuario (id_usuario),
    CONSTRAINT fk_docente_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE periodo
(
    id_periodo     INT         NOT NULL AUTO_INCREMENT,
    nombre_periodo VARCHAR(50) NOT NULL,
    tipo           VARCHAR(10) NOT NULL,
    fecha_inicio   DATE        NOT NULL,
    fecha_fin      DATE        NOT NULL,
    estado         VARCHAR(10) NOT NULL,
    PRIMARY KEY (id_periodo)
) ENGINE=InnoDB;

CREATE TABLE competencia
(
    id_competencia INT          NOT NULL AUTO_INCREMENT,
    descripcion    VARCHAR(255) NOT NULL,
    id_curso       INT          NOT NULL,
    codigo         VARCHAR(10),
    PRIMARY KEY (id_competencia),
    CONSTRAINT fk_competencia_curso FOREIGN KEY (id_curso) REFERENCES curso (id_curso)
) ENGINE=InnoDB;

CREATE TABLE asignacion_curso
(
    id_asignacion  INT         NOT NULL AUTO_INCREMENT,
    anio_academico VARCHAR(10) NOT NULL,
    id_docente     INT         NOT NULL,
    id_curso       INT         NOT NULL,
    id_seccion     INT         NOT NULL,
    PRIMARY KEY (id_asignacion),
    CONSTRAINT fk_asignacion_docente FOREIGN KEY (id_docente) REFERENCES docente (id_docente),
    CONSTRAINT fk_asignacion_curso FOREIGN KEY (id_curso) REFERENCES curso (id_curso),
    CONSTRAINT fk_asignacion_seccion FOREIGN KEY (id_seccion) REFERENCES seccion (id_seccion)
) ENGINE=InnoDB;
CREATE TABLE calificacion
(
    id_calificacion        INT     NOT NULL AUTO_INCREMENT,
    id_estudiante          INT     NOT NULL,
    id_competencia         INT     NOT NULL,
    id_periodo             INT     NOT NULL,
    observacion            VARCHAR(255),
    mes                    TINYINT NOT NULL,
    nivel_mensual          CHAR(2) NOT NULL,
    fortaleza              VARCHAR(150),
    debilidad              VARCHAR(150),
    conclusion_descriptiva TEXT,
    PRIMARY KEY (id_calificacion),
    UNIQUE KEY uk_calificacion (id_estudiante, id_competencia, id_periodo, mes),
    CONSTRAINT fk_calificacion_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
    CONSTRAINT fk_calificacion_competencia FOREIGN KEY (id_competencia) REFERENCES competencia (id_competencia),
    CONSTRAINT fk_calificacion_periodo FOREIGN KEY (id_periodo) REFERENCES periodo (id_periodo)
) ENGINE=InnoDB;

-- FK del tutor de sección (seccion se crea en 02_modulo2.sql)
ALTER TABLE seccion
    ADD CONSTRAINT fk_seccion_tutor FOREIGN KEY (id_docente_tutor) REFERENCES docente (id_docente);