-- YachayKard - Datos de prueba (ficticios)
-- Ejecutar después de los scripts 01 a 05.

USE yachaykard_db;

-- Institución
INSERT INTO institucion (nombre, codigo_modular, nivel, nombre_director, direccion,
                         departamento, provincia, distrito, telefono, correo_electronico)
VALUES ('I.E. "Demo YachayKard"', '1234567', 'Secundaria', 'Director Demo', 'Av. Principal 123',
        'Lima', 'Lima', 'Lima', '014567890', 'contacto@demoyachaykard.edu.pe');

-- Usuarios (uno por rol)
INSERT INTO usuario (username, password, id_rol, id_institucion)
VALUES ('admin',    'Demo2026', (SELECT id_rol FROM rol WHERE nombre_rol = 'Administrador'), 1),
       ('docente',  'Demo2026', (SELECT id_rol FROM rol WHERE nombre_rol = 'Docente'),       1),
       ('auxiliar', 'Demo2026', (SELECT id_rol FROM rol WHERE nombre_rol = 'Auxiliar'),      1);
