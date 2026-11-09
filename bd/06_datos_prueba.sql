-- YachayKard - Datos de prueba
USE yachaykard_db;

-- Institución
INSERT INTO institucion (nombre, codigo_modular, nivel, nombre_director, direccion,
                         departamento, provincia, distrito, telefono, correo_electronico)
VALUES ('I.E. "Demo YachayKard"', '1234567', 'Secundaria', 'Director Demo', 'Av. Principal 123',
        'Lima', 'Lima', 'Lima', '014567890', 'contacto@demoyachaykard.edu.pe');

-- Usuarios (uno por rol). La contraseña de los tres es Demo2026,
-- guardada cifrada con PBKDF2 y sal (RNF-02): formato pbkdf2$iteraciones$sal$hash.
INSERT INTO usuario (username, password, id_rol, id_institucion)
VALUES ('admin',    'pbkdf2$600000$GQ304ME7DDfUY6Ala6NeFg==$PAfgEa3hyxPo3ZaPU/TTTjUg0EhAEvJxNzh0T1vWh4I=', (SELECT id_rol FROM rol WHERE nombre_rol = 'Administrador'), 1),
       ('docente',  'pbkdf2$600000$lccSN6ciYwK0dlhmiqRufA==$NoRnZ5G/56qQKdadEcS/g8mmMrTH/WAIieatGtwKXuc=', (SELECT id_rol FROM rol WHERE nombre_rol = 'Docente'),       1),
       ('auxiliar', 'pbkdf2$600000$D7YLJTYUr86BX5wGgP2qYw==$dX/5v/8leuvaEb7sB8ZjPejpbZBOuOGlDZwBDe3Y81k=', (SELECT id_rol FROM rol WHERE nombre_rol = 'Auxiliar'),      1);
