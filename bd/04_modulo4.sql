-- YachayKard - Módulo 4: Asistencia, Conducta y Cierre
-- Tablas: tipo_incidencia, justificacion, asistencia, incidencia, notificacion, reporte_cierre
-- Ejecutar después de 03_modulo3.sql

USE yachaykard_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reporte_cierre;
DROP TABLE IF EXISTS notificacion;
DROP TABLE IF EXISTS incidencia;
DROP TABLE IF EXISTS asistencia;
DROP TABLE IF EXISTS justificacion;
DROP TABLE IF EXISTS tipo_incidencia;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE tipo_incidencia (
  id_tipo_incidencia INT NOT NULL AUTO_INCREMENT,
  nombre             VARCHAR(100) NOT NULL,
  gravedad_sugerida  VARCHAR(10)  NOT NULL,
  PRIMARY KEY (id_tipo_incidencia)
) ENGINE=InnoDB;

CREATE TABLE justificacion (
  id_justificacion INT NOT NULL AUTO_INCREMENT,
  fecha_inicio     DATE NOT NULL,
  fecha_fin        DATE NOT NULL,
  motivo           VARCHAR(200) NOT NULL,
  evidencia        VARCHAR(255),
  id_estudiante    INT NOT NULL,
  id_usuario       INT NOT NULL,
  PRIMARY KEY (id_justificacion),
  CONSTRAINT fk_justificacion_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_justificacion_usuario    FOREIGN KEY (id_usuario)    REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE asistencia (
  id_asistencia    INT NOT NULL AUTO_INCREMENT,
  fecha            DATE NOT NULL,
  estado           VARCHAR(20) NOT NULL,
  id_estudiante    INT NOT NULL,
  id_auxiliar      INT NOT NULL,
  id_justificacion INT NULL,
  PRIMARY KEY (id_asistencia),
  UNIQUE KEY uk_asistencia (id_estudiante, fecha),
  CONSTRAINT fk_asistencia_estudiante    FOREIGN KEY (id_estudiante)    REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_asistencia_auxiliar      FOREIGN KEY (id_auxiliar)      REFERENCES auxiliar (id_auxiliar),
  CONSTRAINT fk_asistencia_justificacion FOREIGN KEY (id_justificacion) REFERENCES justificacion (id_justificacion)
) ENGINE=InnoDB;

CREATE TABLE incidencia (
  id_incidencia      INT NOT NULL AUTO_INCREMENT,
  fecha_hora         DATETIME NOT NULL,
  descripcion        TEXT,
  id_estudiante      INT NOT NULL,
  id_tipo_incidencia INT NOT NULL,
  gravedad           VARCHAR(10) NOT NULL,
  accion_tomada      VARCHAR(50) NOT NULL,
  id_usuario         INT NOT NULL,
  id_curso           INT NULL,
  requiere_siseve    TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id_incidencia),
  CONSTRAINT fk_incidencia_estudiante FOREIGN KEY (id_estudiante)      REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_incidencia_tipo       FOREIGN KEY (id_tipo_incidencia) REFERENCES tipo_incidencia (id_tipo_incidencia),
  CONSTRAINT fk_incidencia_usuario    FOREIGN KEY (id_usuario)         REFERENCES usuario (id_usuario),
  CONSTRAINT fk_incidencia_curso      FOREIGN KEY (id_curso)           REFERENCES curso (id_curso)
) ENGINE=InnoDB;

CREATE TABLE notificacion (
  id_notificacion INT NOT NULL AUTO_INCREMENT,
  fecha_envio     DATETIME NOT NULL,
  mensaje         TEXT NOT NULL,
  id_apoderado    INT NULL,
  id_usuario      INT NULL,
  id_estudiante   INT NOT NULL,
  canal           VARCHAR(15) NOT NULL,
  tipo            VARCHAR(30) NOT NULL,
  estado          VARCHAR(15) NOT NULL,
  PRIMARY KEY (id_notificacion),
  CONSTRAINT fk_notificacion_apoderado  FOREIGN KEY (id_apoderado)  REFERENCES apoderado (id_apoderado),
  CONSTRAINT fk_notificacion_usuario    FOREIGN KEY (id_usuario)    REFERENCES usuario (id_usuario),
  CONSTRAINT fk_notificacion_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante)
) ENGINE=InnoDB;

CREATE TABLE reporte_cierre (
  id_reporte       INT NOT NULL AUTO_INCREMENT,
  fecha_generacion DATE NOT NULL,
  estado           VARCHAR(15) NOT NULL,
  nivel_final      CHAR(2) NOT NULL,
  id_estudiante    INT NOT NULL,
  id_competencia   INT NOT NULL,
  id_periodo       INT NOT NULL,
  PRIMARY KEY (id_reporte),
  UNIQUE KEY uk_reporte_cierre (id_estudiante, id_competencia, id_periodo),
  CONSTRAINT fk_reporte_estudiante  FOREIGN KEY (id_estudiante)  REFERENCES estudiante (id_estudiante),
  CONSTRAINT fk_reporte_competencia FOREIGN KEY (id_competencia) REFERENCES competencia (id_competencia),
  CONSTRAINT fk_reporte_periodo     FOREIGN KEY (id_periodo)     REFERENCES periodo (id_periodo)
) ENGINE=InnoDB;

-- Catálogo inicial de tipos de incidencia (cada colegio lo edita según su reglamento)
INSERT INTO tipo_incidencia (nombre, gravedad_sugerida) VALUES
  ('Tardanza', 'Leve'),
  ('Uniforme incompleto', 'Leve'),
  ('Uso indebido del celular', 'Leve'),
  ('Falta de respeto', 'Moderada'),
  ('Salida sin permiso', 'Moderada'),
  ('Agresión física', 'Grave'),
  ('Otra', 'Leve');