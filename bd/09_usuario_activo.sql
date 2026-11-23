-- YachayKard - Usuarios activos e inactivos (RF-02)
-- Un usuario desactivado ya no puede iniciar sesión; no se borra para conservar su historial.

USE yachaykard_db;

ALTER TABLE usuario ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;

-- Comprobación: todos los usuarios existentes quedan activos.
SELECT username, activo FROM usuario;