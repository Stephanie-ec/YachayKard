package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.modelo.Apoderado;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de los datos del apoderado (RF-11). El teléfono se usa para los avisos por WhatsApp,
 * por eso debe ser un celular peruano: 9 dígitos que empiezan con 9.
 */
public class ValidadorApoderado {

    private static final Pattern DNI = Pattern.compile("\\d{8}");
    private static final Pattern CELULAR = Pattern.compile("9\\d{8}");

    /**
     * Revisa los datos del apoderado.
     *
     * @param apoderado  datos a revisar
     * @param existentes apoderados ya registrados (para no repetir el DNI)
     * @return lista de errores; vacía si todo es correcto
     */
    public List<String> validar(Apoderado apoderado, List<Apoderado> existentes) {
        List<String> errores = new ArrayList<>();
        if (apoderado == null) {
            errores.add("No hay datos del apoderado.");
            return errores;
        }
        String dni = apoderado.getDni();
        if (dni == null || !DNI.matcher(dni).matches()) {
            errores.add("El DNI del apoderado debe tener 8 dígitos.");
        } else if (existentes.stream().anyMatch(a -> dni.equals(a.getDni())
                && a.getIdApoderado() != apoderado.getIdApoderado())) {
            errores.add("Ya existe un apoderado con ese DNI.");
        }
        if (apoderado.getNombres() == null || apoderado.getNombres().isBlank()) {
            errores.add("Los nombres del apoderado son obligatorios.");
        }
        String telefono = apoderado.getTelefono();
        if (telefono != null && !telefono.isBlank() && !CELULAR.matcher(telefono).matches()) {
            errores.add("El celular debe tener 9 dígitos y empezar con 9.");
        }
        return errores;
    }
}
