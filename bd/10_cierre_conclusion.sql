USE yachaykard_db;

ALTER TABLE reporte_cierre ADD COLUMN conclusion_descriptiva TEXT NULL;

-- Comprobación: la tabla debe mostrar la columna nueva al final.
DESCRIBE reporte_cierre;