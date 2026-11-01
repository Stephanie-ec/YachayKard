package pe.edu.utp.yachaykard.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Fecha y hora oficiales del sistema (hora de Lima).
 * Evita depender de la zona horaria del servidor.
 */
public final class Zona {

    /** Zona horaria del Perú. */
    public static final ZoneId LIMA = ZoneId.of("America/Lima");

    private Zona() {
        // Clase de utilidad: no se instancia.
    }

    /** @return la fecha de hoy en Lima */
    public static LocalDate hoy() {
        return LocalDate.now(LIMA);
    }

    /** @return la fecha y hora actuales en Lima */
    public static LocalDateTime ahora() {
        return LocalDateTime.now(LIMA);
    }
}
