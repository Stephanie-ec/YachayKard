package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Periodo;

import java.util.Locale;

/**
 * Códigos y textos que pide la plantilla RegNotas del SIAGIE (hojas Generalidades y Parametros)
 * a partir de los datos de YachayKard. Por ejemplo, 4° secundaria es grado "13" y "CUARTO",
 * la sección C es "03" y el II Trimestre es periodo "T2" y "SEGUNDO TRIMESTRE".
 */
public final class CodigosSiagie {

    private static final String[] GRADOS = {"", "PRIMERO", "SEGUNDO", "TERCERO", "CUARTO", "QUINTO"};
    private static final String[] ORDINALES = {"", "PRIMER", "SEGUNDO", "TERCER", "CUARTO"};
    private static final String[] ROMANOS = {"", "I", "II", "III", "IV"};
    /** En la plantilla, 1° de secundaria es el grado 10 (4° es el 13). */
    private static final int BASE_GRADO_SECUNDARIA = 9;

    private CodigosSiagie() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * @param numeroGrado grado como se guarda en YachayKard (por ejemplo "4°")
     * @return número de grado de 1 a 5
     * @throws IllegalArgumentException si no es un grado de secundaria
     */
    static int numeroGrado(String numeroGrado) {
        String digitos = numeroGrado == null ? "" : numeroGrado.replaceAll("\\D", "");
        if (digitos.isEmpty() || Integer.parseInt(digitos) < 1 || Integer.parseInt(digitos) > 5) {
            throw new IllegalArgumentException("Grado no válido para secundaria: " + numeroGrado);
        }
        return Integer.parseInt(digitos);
    }

    /**
     * @param numeroGrado por ejemplo "4°"
     * @return nombre del grado en la plantilla, por ejemplo "CUARTO"
     */
    public static String nombreGrado(String numeroGrado) {
        return GRADOS[numeroGrado(numeroGrado)];
    }

    /**
     * @param numeroGrado por ejemplo "4°"
     * @return código del grado en la hoja Parametros, por ejemplo "13"
     */
    public static String codigoGrado(String numeroGrado) {
        return String.valueOf(BASE_GRADO_SECUNDARIA + numeroGrado(numeroGrado));
    }

    /**
     * @param letra letra de la sección
     * @return código de dos dígitos: A es "01", C es "03"
     */
    public static String codigoSeccion(char letra) {
        char mayuscula = Character.toUpperCase(letra);
        if (mayuscula < 'A' || mayuscula > 'Z') {
            throw new IllegalArgumentException("Sección no válida: " + letra);
        }
        return String.format("%02d", mayuscula - 'A' + 1);
    }

    /**
     * @param periodo periodo cuyo nombre empieza con el número romano (por ejemplo "II Trimestre")
     * @return número del periodo de 1 a 4
     * @throws IllegalArgumentException si el nombre no empieza con I, II, III o IV
     */
    static int numeroPeriodo(Periodo periodo) {
        String nombre = periodo.getNombrePeriodo() == null ? "" : periodo.getNombrePeriodo().trim();
        String primero = nombre.isEmpty() ? "" : nombre.split("\\s+")[0].toUpperCase(Locale.ROOT);
        for (int i = 1; i < ROMANOS.length; i++) {
            if (ROMANOS[i].equals(primero)) {
                return i;
            }
        }
        throw new IllegalArgumentException("El nombre del periodo debe empezar con I, II, III o IV: " + nombre);
    }

    /**
     * @param periodo por ejemplo "II Trimestre" de tipo Trimestre
     * @return nombre en la plantilla, por ejemplo "SEGUNDO TRIMESTRE"
     */
    public static String nombrePeriodo(Periodo periodo) {
        return ORDINALES[numeroPeriodo(periodo)] + " " + periodo.getTipo().toUpperCase(Locale.ROOT);
    }

    /**
     * @param periodo por ejemplo "II Trimestre" de tipo Trimestre
     * @return código en la hoja Parametros: T1 a T3 para trimestres, B1 a B4 para bimestres
     */
    public static String codigoPeriodo(Periodo periodo) {
        String letra = "Bimestre".equalsIgnoreCase(periodo.getTipo()) ? "B" : "T";
        return letra + numeroPeriodo(periodo);
    }

    /**
     * @param nombreHoja nombre de la hoja del libro, por ejemplo "063-MATE"
     * @return código del área ("063"), o null si la hoja no es de un área
     */
    public static String codigoHoja(String nombreHoja) {
        int guion = nombreHoja == null ? -1 : nombreHoja.indexOf('-');
        if (guion <= 0) {
            return null;
        }
        String codigo = nombreHoja.substring(0, guion);
        return codigo.chars().allMatch(Character::isDigit) ? codigo : null;
    }

    /**
     * Columna del nivel de logro (NL) de una competencia. La competencia 01 va en la columna D,
     * la 02 en la F, la 03 en la H y la 04 en la J; la conclusión va en la columna siguiente.
     *
     * @param codigoCompetencia por ejemplo "063-04"
     * @return índice de la columna (D es 3), o -1 si el código no tiene número de competencia
     */
    public static int columnaNivel(String codigoCompetencia) {
        int guion = codigoCompetencia == null ? -1 : codigoCompetencia.lastIndexOf('-');
        if (guion < 0) {
            return -1;
        }
        try {
            int numero = Integer.parseInt(codigoCompetencia.substring(guion + 1));
            return numero < 1 ? -1 : 3 + 2 * (numero - 1);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * @param estudiante estudiante
     * @return "APELLIDOS, NOMBRES" en mayúsculas, como en la nómina del SIAGIE
     */
    public static String nombreEstudiante(Estudiante estudiante) {
        return (estudiante.getApellidos() + ", " + estudiante.getNombres()).toUpperCase(Locale.ROOT);
    }
}