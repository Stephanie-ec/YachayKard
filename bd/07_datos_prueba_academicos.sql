-- YachayKard - Datos de prueba académicos (ficticios)
-- Ejecutar después de 06_datos_prueba.sql.
-- Sección 1° A: tutor = usuario "docente". Sección 1° B: sin tutor
-- (sirve para comprobar que el docente no puede ver el historial de 1° B).

USE yachaykard_db;

SET @u_admin = (SELECT id_usuario FROM usuario WHERE username = 'admin');
SET @u_doc   = (SELECT id_usuario FROM usuario WHERE username = 'docente');
SET @u_aux   = (SELECT id_usuario FROM usuario WHERE username = 'auxiliar');

-- Personal
INSERT INTO docente (dni, nombres, apellidos, especialidad, id_usuario)
VALUES ('40123456', 'Luis Alberto', 'Mendoza Ríos', 'Matemática', @u_doc);
SET @docente = (SELECT id_docente FROM docente WHERE id_usuario = @u_doc);

INSERT INTO auxiliar (dni, nombres, apellidos, id_usuario)
VALUES ('41234567', 'Rosa Elena', 'Paredes Vega', @u_aux);
SET @aux = (SELECT id_auxiliar FROM auxiliar WHERE id_usuario = @u_aux);

-- Grados, cursos y competencias
INSERT INTO grado (numero_grado, nivel) VALUES ('1°', 'Secundaria'), ('2°', 'Secundaria');
SET @grado1 = (SELECT id_grado FROM grado WHERE numero_grado = '1°' AND nivel = 'Secundaria');

INSERT INTO curso (nombre_curso, horas_semanales, codigo_siagie)
VALUES ('Matemática', 5, 'MAT'), ('Comunicación', 5, 'COM');
SET @mat = (SELECT id_curso FROM curso WHERE nombre_curso = 'Matemática');
SET @com = (SELECT id_curso FROM curso WHERE nombre_curso = 'Comunicación');

INSERT INTO competencia (descripcion, id_curso, codigo) VALUES
     ('Resuelve problemas de cantidad', @mat, 'MAT-01'),
     ('Resuelve problemas de regularidad, equivalencia y cambio', @mat, 'MAT-02'),
     ('Lee diversos tipos de textos escritos', @com, 'COM-01'),
     ('Escribe diversos tipos de textos', @com, 'COM-02');
SET @c_mat1 = (SELECT id_competencia FROM competencia WHERE codigo = 'MAT-01');
SET @c_com1 = (SELECT id_competencia FROM competencia WHERE codigo = 'COM-01');

-- Secciones
INSERT INTO seccion (letra, aforo, id_grado, id_docente_tutor, id_auxiliar) VALUES
     ('A', 30, @grado1, @docente, @aux),
     ('B', 30, @grado1, NULL, @aux);
SET @secA = (SELECT id_seccion FROM seccion WHERE id_grado = @grado1 AND letra = 'A');
SET @secB = (SELECT id_seccion FROM seccion WHERE id_grado = @grado1 AND letra = 'B');

-- Periodos 2026
INSERT INTO periodo (nombre_periodo, tipo, fecha_inicio, fecha_fin, estado) VALUES
     ('I Trimestre',   'Trimestre', '2026-03-09', '2026-06-12', 'Cerrado'),
     ('II Trimestre',  'Trimestre', '2026-06-15', '2026-09-11', 'Cerrado'),
     ('III Trimestre', 'Trimestre', '2026-09-14', '2026-12-18', 'Abierto');
SET @p1 = (SELECT id_periodo FROM periodo WHERE nombre_periodo = 'I Trimestre');
SET @p3 = (SELECT id_periodo FROM periodo WHERE nombre_periodo = 'III Trimestre');

-- Asignación de cursos al docente en 1° A
INSERT INTO asignacion_curso (anio_academico, id_docente, id_curso, id_seccion) VALUES
     ('2026', @docente, @mat, @secA),
     ('2026', @docente, @com, @secA);

-- Apoderados
INSERT INTO apoderado (dni, nombres, telefono) VALUES
     ('45678901', 'María Torres Salas',   '987654321'),
     ('46789012', 'Jorge Huamán Cruz',    '976543210'),
     ('47890123', 'Ana Flores Rojas',     '965432109'),
     ('48901234', 'Pedro Castillo Núñez', NULL),
     ('49012345', 'Lucía Ramos Peña',     '954321098'),
     ('40987654', 'Carlos Díaz León',     '943210987');

-- Estudiantes
INSERT INTO estudiante (dni, nombres, apellidos, fecha_nacimiento, id_apoderado, id_siagie, codigo_estudiante) VALUES
     ('71234501', 'Valeria',  'Torres Salas',   '2013-04-15', (SELECT id_apoderado FROM apoderado WHERE dni = '45678901'), 'S0001', '00000000000001'),
     ('71234502', 'Diego',    'Huamán Cruz',    '2013-07-22', (SELECT id_apoderado FROM apoderado WHERE dni = '46789012'), 'S0002', '00000000000002'),
     ('71234503', 'Camila',   'Flores Rojas',   '2013-01-30', (SELECT id_apoderado FROM apoderado WHERE dni = '47890123'), 'S0003', '00000000000003'),
     ('71234504', 'Mateo',    'Castillo Núñez', '2013-10-05', (SELECT id_apoderado FROM apoderado WHERE dni = '48901234'), 'S0004', '00000000000004'),
     ('71234505', 'Sofía',    'Ramos Peña',     '2013-03-12', (SELECT id_apoderado FROM apoderado WHERE dni = '49012345'), 'S0005', '00000000000005'),
     ('71234506', 'Sebastián','Díaz León',      '2013-09-18', (SELECT id_apoderado FROM apoderado WHERE dni = '40987654'), 'S0006', '00000000000006');

SET @e1 = (SELECT id_estudiante FROM estudiante WHERE dni = '71234501');
SET @e2 = (SELECT id_estudiante FROM estudiante WHERE dni = '71234502');
SET @e3 = (SELECT id_estudiante FROM estudiante WHERE dni = '71234503');
SET @e4 = (SELECT id_estudiante FROM estudiante WHERE dni = '71234504');
SET @e5 = (SELECT id_estudiante FROM estudiante WHERE dni = '71234505');
SET @e6 = (SELECT id_estudiante FROM estudiante WHERE dni = '71234506');

-- Matrículas 2026: cuatro en 1° A y dos en 1° B
INSERT INTO matricula (fecha_matricula, anio_lectivo, estado, id_estudiante, id_seccion) VALUES
     ('2026-03-02', '2026', 'Activa', @e1, @secA),
     ('2026-03-02', '2026', 'Activa', @e2, @secA),
     ('2026-03-02', '2026', 'Activa', @e3, @secA),
     ('2026-03-02', '2026', 'Activa', @e4, @secA),
     ('2026-03-03', '2026', 'Activa', @e5, @secB),
     ('2026-03-03', '2026', 'Activa', @e6, @secB);

-- Calificaciones: I Trimestre (cerrado) y octubre del III Trimestre (dos estudiantes pendientes)
INSERT INTO calificacion (id_estudiante, id_competencia, id_periodo, mes, nivel_mensual, fortaleza, debilidad, conclusion_descriptiva) VALUES
     (@e1, @c_mat1, @p1, 5, 'AD', 'Razona con claridad', NULL,
     'El estudiante alcanzó el nivel AD en Resuelve problemas de cantidad. Fortaleza: Razona con claridad.'),
     (@e2, @c_mat1, @p1, 5, 'B', NULL, 'Requiere reforzar los procedimientos',
     'El estudiante alcanzó el nivel B en Resuelve problemas de cantidad. A mejorar: Requiere reforzar los procedimientos.'),
     (@e4, @c_mat1, @p1, 5, 'C', NULL, 'No completa sus tareas',
     'El estudiante alcanzó el nivel C en Resuelve problemas de cantidad. A mejorar: No completa sus tareas.'),
     (@e1, @c_mat1, @p3, 10, 'A', 'Participa activamente en clase', NULL,
     'El estudiante alcanzó el nivel A en Resuelve problemas de cantidad. Fortaleza: Participa activamente en clase.'),
     (@e2, @c_mat1, @p3, 10, 'B', 'Trabaja bien en equipo', 'Dificultad para argumentar sus respuestas',
     'El estudiante alcanzó el nivel B en Resuelve problemas de cantidad. Fortaleza: Trabaja bien en equipo. A mejorar: Dificultad para argumentar sus respuestas.'),
     (@e1, @c_com1, @p1, 5, 'A', 'Entrega sus trabajos a tiempo', NULL,
     'El estudiante alcanzó el nivel A en Lee diversos tipos de textos escritos. Fortaleza: Entrega sus trabajos a tiempo.');

-- Justificación de una falta
INSERT INTO justificacion (fecha_inicio, fecha_fin, motivo, evidencia, id_estudiante, id_usuario)
VALUES ('2026-10-06', '2026-10-06', 'Cita médica', NULL, @e2, @u_aux);
SET @just = (SELECT id_justificacion FROM justificacion WHERE id_estudiante = @e2 AND fecha_inicio = '2026-10-06');

-- Asistencia de la semana del 5 al 9 de octubre (1° A)
INSERT INTO asistencia (fecha, estado, id_estudiante, id_auxiliar, id_justificacion) VALUES
     ('2026-10-05', 'Presente', @e1, @aux, NULL), ('2026-10-06', 'Presente', @e1, @aux, NULL),
     ('2026-10-07', 'Presente', @e1, @aux, NULL), ('2026-10-08', 'Presente', @e1, @aux, NULL),
     ('2026-10-09', 'Presente', @e1, @aux, NULL),
     ('2026-10-05', 'Presente', @e2, @aux, NULL), ('2026-10-06', 'Falta', @e2, @aux, @just),
     ('2026-10-07', 'Tardanza', @e2, @aux, NULL), ('2026-10-08', 'Presente', @e2, @aux, NULL),
     ('2026-10-09', 'Presente', @e2, @aux, NULL),
     ('2026-10-05', 'Presente', @e3, @aux, NULL), ('2026-10-06', 'Presente', @e3, @aux, NULL),
     ('2026-10-07', 'Presente', @e3, @aux, NULL), ('2026-10-08', 'Tardanza', @e3, @aux, NULL),
     ('2026-10-09', 'Presente', @e3, @aux, NULL),
     ('2026-10-05', 'Falta', @e4, @aux, NULL), ('2026-10-06', 'Falta', @e4, @aux, NULL),
     ('2026-10-07', 'Tardanza', @e4, @aux, NULL), ('2026-10-08', 'Falta', @e4, @aux, NULL),
     ('2026-10-09', 'Falta', @e4, @aux, NULL);

-- Incidencias
INSERT INTO incidencia (fecha_hora, descripcion, id_estudiante, id_tipo_incidencia, gravedad, accion_tomada, id_usuario, id_curso, requiere_siseve) VALUES
     ('2026-10-07 08:10:00', 'Llegó 15 minutos tarde a la primera hora.', @e4,
     (SELECT id_tipo_incidencia FROM tipo_incidencia WHERE nombre = 'Tardanza'), 'Leve', 'Llamada de atención', @u_doc, @mat, 0),
     ('2026-10-08 10:30:00', 'Usó el celular durante la evaluación.', @e4,
     (SELECT id_tipo_incidencia FROM tipo_incidencia WHERE nombre = 'Uso indebido del celular'), 'Leve', 'Diálogo con el estudiante', @u_doc, @mat, 0),
     ('2026-10-09 11:00:00', 'Respondió de forma irrespetuosa a un compañero.', @e2,
     (SELECT id_tipo_incidencia FROM tipo_incidencia WHERE nombre = 'Falta de respeto'), 'Moderada', 'Derivación a Tutoría', @u_doc, @com, 0);

-- Predicciones de riesgo
INSERT INTO prediccion_riesgo (id_estudiante, nivel_riesgo, motivo, fecha_calculo) VALUES
     (@e4, 'Alto',  'Cuatro faltas en la semana, nivel C en Matemática y dos incidencias.', '2026-10-10'),
     (@e2, 'Medio', 'Nivel B en Matemática y una incidencia moderada.',                      '2026-10-10'),
     (@e1, 'Bajo',  'Asistencia completa y buen rendimiento.',                               '2026-10-10');
