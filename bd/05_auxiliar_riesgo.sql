-- YachayKard - PrediccionRiesgo
-- Ejecutar al final: depende de estudiante (02_modulo2.sql)

USE yachaykard_db;

DROP TABLE IF EXISTS prediccion_riesgo;

CREATE TABLE prediccion_riesgo (
    id_prediccion INT NOT NULL AUTO_INCREMENT,
    id_estudiante INT NOT NULL,
    nivel_riesgo  VARCHAR(10)  NOT NULL,
    motivo        VARCHAR(255),
    fecha_calculo DATE NOT NULL,
    PRIMARY KEY (id_prediccion),
    CONSTRAINT fk_prediccion_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante)
) ENGINE=InnoDB;