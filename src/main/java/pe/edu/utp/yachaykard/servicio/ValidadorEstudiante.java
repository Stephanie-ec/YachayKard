package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.util.Zona;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de validación de los datos de un estudiante (RF-10).
 * Se revisan en el servidor aunque la pantalla ya las valide, para que
 * ningún dato incorrecto llegue a la base de datos.
 */
public class ValidadorEstudiante {

    /** DNI peruano: exactamente 8 dígitos. */
    private static final Pattern DNI = Pattern.compile("\\d{8}");

    /** Código de estudiante del SIAGIE: exactamente 14 dígitos. */
    private static final Pattern CODIGO_ESTUDIANTE = Pattern.compile("\\d{14}");

    /**
     * Revisa los datos del estudiante.
     *
     * @param estudiante datos a revisar
     * @return lista de errores; vacía si todo es correcto
     */
    public List<String> validar(Estudiante estudiante) {
        List<String> errores = new ArrayList<>();
        if (estudiante == null) {
            errores.add("No hay datos del estudiante.");
            return errores;
        }
        if (!dniValido(estudiante.getDni())) {
            errores.add("El DNI debe tener 8 dígitos.");
        }
        if (vacio(estudiante.getNombres())) {
            errores.add("Los nombres son obligatorios.");
        }
        if (vacio(estudiante.getApellidos())) {
            errores.add("Los apellidos son obligatorios.");
        }
        if (!vacio(estudiante.getCodigoEstudiante()) && !codigoValido(estudiante.getCodigoEstudiante())) {
            errores.add("El código de estudiante debe tener 14 dígitos.");
        }
        if (estudiante.getFechaNacimiento() != null && estudiante.getFechaNacimiento().isAfter(Zona.hoy())) {
            errores.add("La fecha de nacimiento no puede ser posterior a hoy.");
        }
        if (estudiante.getIdApoderado() <= 0) {
            errores.add("Seleccione un apoderado.");
        }
        return errores;
    }

    /**
     * @param dni texto a revisar
     * @return true si tiene exactamente 8 dígitos
     */
    public boolean dniValido(String dni) {
        return dni != null && DNI.matcher(dni).matches();
    }

    /**
     * @param codigo texto a revisar
     * @return true si tiene exactamente 14 dígitos
     */
    public boolean codigoValido(String codigo) {
        return codigo != null && CODIGO_ESTUDIANTE.matcher(codigo).matches();
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
