package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Reglas del registro de incidencias (RF-23):
 * <ul>
 *     <li>El tipo debe estar activo en el catálogo.</li>
 *     <li>La acción tomada es obligatoria y se elige de una lista.</li>
 *     <li>La descripción es obligatoria (mínimo 10 caracteres) si el tipo es "Otra" o la gravedad es Grave.</li>
 *     <li>Una incidencia grave se marca siempre para reportar en el SíseVe.</li>
 *     <li>La fecha y hora las pone el sistema (hora de Lima).</li>
 * </ul>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class ReglasIncidencia {

    public static final String GRAVE = "Grave";
    static final Set<String> GRAVEDADES = Set.of("Leve", "Moderada", GRAVE);
    static final int DESCRIPCION_MINIMA = 10;

    /** Acciones que se pueden tomar (caben en la columna accion_tomada de 50 caracteres). */
    public static final List<String> ACCIONES = List.of(
            "Diálogo con el estudiante",
            "Comunicación al apoderado",
            "Citación al apoderado",
            "Derivación a tutoría",
            "Derivación a psicología",
            "Derivación a dirección",
            "Otra acción");

    private final IncidenciaDAO incidenciaDAO;
    private final TipoIncidenciaDAO tipoDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public ReglasIncidencia() {
        this(new IncidenciaDAOImpl(), new TipoIncidenciaDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param incidenciaDAO acceso a incidencias
     * @param tipoDAO       acceso al catálogo de tipos
     */
    public ReglasIncidencia(IncidenciaDAO incidenciaDAO, TipoIncidenciaDAO tipoDAO) {
        this.incidenciaDAO = incidenciaDAO;
        this.tipoDAO = tipoDAO;
    }

    /**
     * Registra una incidencia si cumple las reglas.
     *
     * @param incidencia datos del formulario (estudiante, tipo, gravedad, acción, descripción)
     * @param ahora      fecha y hora del registro
     * @throws IllegalArgumentException si un dato no cumple las reglas
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void registrar(Incidencia incidencia, LocalDateTime ahora) throws SQLException {
        if (incidencia.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("Seleccione al estudiante.");
        }
        TipoIncidencia tipo = incidencia.getIdTipoIncidencia() > 0
                ? tipoDAO.buscarPorId(incidencia.getIdTipoIncidencia()) : null;
        if (tipo == null || !tipo.isActivo()) {
            throw new IllegalArgumentException("Seleccione un tipo de incidencia activo.");
        }
        if (incidencia.getGravedad() == null || !GRAVEDADES.contains(incidencia.getGravedad())) {
            throw new IllegalArgumentException("La gravedad debe ser Leve, Moderada o Grave.");
        }
        if (incidencia.getAccionTomada() == null || !ACCIONES.contains(incidencia.getAccionTomada())) {
            throw new IllegalArgumentException("Seleccione la acción tomada.");
        }
        String descripcion = incidencia.getDescripcion() == null ? "" : incidencia.getDescripcion().trim();
        boolean pideDescripcion = CatalogoIncidencias.TIPO_OTRA.equalsIgnoreCase(tipo.getNombre())
                || GRAVE.equals(incidencia.getGravedad());
        if (pideDescripcion && descripcion.length() < DESCRIPCION_MINIMA) {
            throw new IllegalArgumentException("Describa lo ocurrido (mínimo " + DESCRIPCION_MINIMA
                    + " caracteres) cuando el tipo es \"Otra\" o la gravedad es Grave.");
        }
        incidencia.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        if (GRAVE.equals(incidencia.getGravedad())) {
            incidencia.setRequiereSiseve(true);
        }
        incidencia.setFechaHora(ahora);
        incidenciaDAO.insertar(incidencia);
    }
}
