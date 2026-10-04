package pe.edu.utp.yachaykard.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centraliza la conexión JDBC a la base de datos
 * Las credenciales se leen desde db.properties, archivo excluido del
 * control de versiones para no exponer contraseñas en GitHub.
 */
public class ConexionBD {

    /** Nombre del archivo de configuración ubicado en src/main/resources. */
    private static final String ARCHIVO = "db.properties";

    /** Propiedades de conexión cargadas una sola vez al iniciar la clase. */
    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream entrada = ConexionBD.class.getClassLoader().getResourceAsStream(ARCHIVO)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontró " + ARCHIVO
                        + " en src/main/resources. Copiar desde db.properties.example.");
            }
            CONFIG.load(entrada);
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer " + ARCHIVO, e);
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.usuario"),
                CONFIG.getProperty("db.password"));
    }
}