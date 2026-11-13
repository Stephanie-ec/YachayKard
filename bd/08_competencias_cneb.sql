-- YachayKard - Competencias del Currículo Nacional (CNEB), nivel secundaria
-- Fuente: plantilla RegNotas del SIAGIE (una hoja por área) contrastada con el programa curricular.
-- Ejecutar después de los scripts 01 a 07. Se ejecuta una sola vez.
--
-- curso.codigo_siagie   = código de la hoja de RegNotas (por ejemplo 063 en "063-MATE").
-- competencia.codigo    = código de la hoja + número de columna (por ejemplo 063-01).
-- No se carga "Castellano como segunda lengua" (0002): es para zonas bilingües.
-- Las competencias transversales (0006 y 0007) son la nota de Tutoría y las registra
-- el tutor de la sección; el administrador se las asigna como cursos de su sección.

USE
yachaykard_db;

-- 1. Matemática y Comunicación ya existen (script 07): se actualizan sus códigos
--    y se completan sus competencias, sin borrar las calificaciones de prueba.
UPDATE curso
SET codigo_siagie = '063'
WHERE nombre_curso = 'Matemática';
UPDATE curso
SET codigo_siagie = '017'
WHERE nombre_curso = 'Comunicación';

UPDATE competencia
SET codigo      = '063-01',
    descripcion = 'Resuelve problemas de cantidad'
WHERE codigo = 'MAT-01';
UPDATE competencia
SET codigo      = '063-02',
    descripcion = 'Resuelve problemas de regularidad, equivalencia y cambio'
WHERE codigo = 'MAT-02';
UPDATE competencia
SET codigo      = '017-02',
    descripcion = 'Lee diversos tipos de textos escritos en su lengua materna'
WHERE codigo = 'COM-01';
UPDATE competencia
SET codigo      = '017-03',
    descripcion = 'Escribe diversos tipos de textos en su lengua materna'
WHERE codigo = 'COM-02';

-- 2. Áreas nuevas (horas semanales de la jornada escolar completa).
INSERT INTO curso (nombre_curso, horas_semanales, codigo_siagie)
VALUES ('Arte y Cultura', 2, '0001'),
       ('Ciencia y Tecnología', 5, '0004'),
       ('Desarrollo Personal, Ciudadanía y Cívica', 3, '0010'),
       ('Ciencias Sociales', 3, '014'),
       ('Educación Física', 3, '031'),
       ('Educación para el Trabajo', 3, '032'),
       ('Educación Religiosa', 2, '035'),
       ('Inglés como Lengua Extranjera', 5, '057'),
       ('Se desenvuelve en entornos virtuales generados por las TIC', 0, '0006'),
       ('Gestiona su aprendizaje de manera autónoma', 0, '0007');

-- 3. Competencias de cada área, en el orden de las columnas de RegNotas.
SET
@mate = (SELECT id_curso FROM curso WHERE codigo_siagie = '063');
SET
@comu = (SELECT id_curso FROM curso WHERE codigo_siagie = '017');
SET
@arte = (SELECT id_curso FROM curso WHERE codigo_siagie = '0001');
SET
@cyt  = (SELECT id_curso FROM curso WHERE codigo_siagie = '0004');
SET
@dpcc = (SELECT id_curso FROM curso WHERE codigo_siagie = '0010');
SET
@ccss = (SELECT id_curso FROM curso WHERE codigo_siagie = '014');
SET
@efis = (SELECT id_curso FROM curso WHERE codigo_siagie = '031');
SET
@etra = (SELECT id_curso FROM curso WHERE codigo_siagie = '032');
SET
@erel = (SELECT id_curso FROM curso WHERE codigo_siagie = '035');
SET
@ingl = (SELECT id_curso FROM curso WHERE codigo_siagie = '057');
SET
@tic  = (SELECT id_curso FROM curso WHERE codigo_siagie = '0006');
SET
@gest = (SELECT id_curso FROM curso WHERE codigo_siagie = '0007');

INSERT INTO competencia (descripcion, id_curso, codigo)
VALUES ('Resuelve problemas de forma, movimiento y localización', @mate, '063-03'),
       ('Resuelve problemas de gestión de datos e incertidumbre', @mate, '063-04'),
       ('Se comunica oralmente en su lengua materna', @comu, '017-01'),
       ('Aprecia de manera crítica manifestaciones artístico-culturales', @arte, '0001-01'),
       ('Crea proyectos desde los lenguajes artísticos', @arte, '0001-02'),
       ('Indaga mediante métodos científicos para construir sus conocimientos', @cyt, '0004-01'),
       ('Explica el mundo físico basándose en conocimientos sobre los seres vivos, materia y energía, biodiversidad, Tierra y universo',
        @cyt, '0004-02'),
       ('Diseña y construye soluciones tecnológicas para resolver problemas de su entorno', @cyt, '0004-03'),
       ('Construye su identidad', @dpcc, '0010-01'),
       ('Convive y participa democráticamente en la búsqueda del bien común', @dpcc, '0010-02'),
       ('Construye interpretaciones históricas', @ccss, '014-01'),
       ('Gestiona responsablemente el espacio y el ambiente', @ccss, '014-02'),
       ('Gestiona responsablemente los recursos económicos', @ccss, '014-03'),
       ('Se desenvuelve de manera autónoma a través de su motricidad', @efis, '031-01'),
       ('Asume una vida saludable', @efis, '031-02'),
       ('Interactúa a través de sus habilidades sociomotrices', @efis, '031-03'),
       ('Gestiona proyectos de emprendimiento económico o social', @etra, '032-01'),
       ('Construye su identidad como persona humana, amada por Dios, digna, libre y trascendente', @erel, '035-01'),
       ('Asume la experiencia del encuentro personal y comunitario con Dios en su proyecto de vida', @erel, '035-02'),
       ('Se comunica oralmente en inglés como lengua extranjera', @ingl, '057-01'),
       ('Lee diversos tipos de textos escritos en inglés como lengua extranjera', @ingl, '057-02'),
       ('Escribe diversos tipos de textos en inglés como lengua extranjera', @ingl, '057-03'),
       ('Se desenvuelve en entornos virtuales generados por las TIC', @tic, '0006-01'),
       ('Gestiona su aprendizaje de manera autónoma', @gest, '0007-01');

-- Comprobación: debe mostrar 12 cursos y 28 competencias en total.
SELECT cu.codigo_siagie, cu.nombre_curso, COUNT(co.id_competencia) AS competencias
FROM curso cu
         LEFT JOIN competencia co ON co.id_curso = cu.id_curso
GROUP BY cu.id_curso, cu.codigo_siagie, cu.nombre_curso
ORDER BY cu.codigo_siagie;