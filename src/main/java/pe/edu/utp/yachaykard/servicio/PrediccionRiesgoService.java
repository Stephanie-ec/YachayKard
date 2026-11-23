package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.CalificacionDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.dao.PrediccionRiesgoDAO;
import pe.edu.utp.yachaykard.dao.impl.AsistenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CalificacionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PrediccionRiesgoDAOImpl;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Calificacion;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cálculo del riesgo de deserción con reglas. Revisa tres señales de alerta:
 * <ol>
 *     <li>Asistencia: 3 o más faltas sin justificar en los últimos 30 días.</li>
 *     <li>Rendimiento: al menos una competencia cuya calificación más reciente es C.</li>
 *     <li>Conducta: al menos una incidencia grave en los últimos 60 días.</li>
 * </ol>
 * Sin señales el riesgo es Bajo; con una, Medio; con dos o tres, Alto.
 * En la semana 4 se agrega el modelo de Weka (RF-26); estas reglas quedan como respaldo.
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class PrediccionRiesgoService {

    public static final String ALTO = "Alto";
    public static final String MEDIO = "Medio";
    public static final String BAJO = "Bajo";

    static final int FALTAS_PARA_ALERTA = 3;
    static final int DIAS_ASISTENCIA = 30;
    static final int DIAS_INCIDENCIAS = 60;

    private final EstudianteDAO estudianteDAO;
    private final AsistenciaDAO asistenciaDAO;
    private final CalificacionDAO calificacionDAO;
    private final IncidenciaDAO incidenciaDAO;
    private final PrediccionRiesgoDAO prediccionDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public PrediccionRiesgoService() {
        this(new EstudianteDAOImpl(), new AsistenciaDAOImpl(), new CalificacionDAOImpl(),
                new IncidenciaDAOImpl(), new PrediccionRiesgoDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param estudianteDAO   acceso a estudiantes
     * @param asistenciaDAO   acceso a asistencia
     * @param calificacionDAO acceso a calificaciones
     * @param incidenciaDAO   acceso a incidencias
     * @param prediccionDAO   acceso a predicciones
     */
    public PrediccionRiesgoService(EstudianteDAO estudianteDAO, AsistenciaDAO asistenciaDAO,
                                   CalificacionDAO calificacionDAO, IncidenciaDAO incidenciaDAO,
                                   PrediccionRiesgoDAO prediccionDAO) {
        this.estudianteDAO = estudianteDAO;
        this.asistenciaDAO = asistenciaDAO;
        this.calificacionDAO = calificacionDAO;
        this.incidenciaDAO = incidenciaDAO;
        this.prediccionDAO = prediccionDAO;
    }

    /**
     * Calcula y guarda el riesgo de un estudiante.
     *
     * @param idEstudiante estudiante
     * @param hoy          fecha del cálculo (en la aplicación, la fecha de Lima)
     * @return predicción guardada
     * @throws SQLException si ocurre un error en la base de datos
     */
    public PrediccionRiesgo calcular(int idEstudiante, LocalDate hoy) throws SQLException {
        int faltas = faltasSinJustificar(asistenciaDAO.listarPorEstudiante(idEstudiante), hoy);
        int competenciasEnC = competenciasEnC(calificacionDAO.listarPorEstudiante(idEstudiante));
        int graves = incidenciasGraves(incidenciaDAO.listarPorEstudiante(idEstudiante), hoy);

        PrediccionRiesgo prediccion = new PrediccionRiesgo();
        prediccion.setIdEstudiante(idEstudiante);
        prediccion.setNivelRiesgo(nivel(faltas, competenciasEnC, graves));
        prediccion.setMotivo(motivo(faltas, competenciasEnC, graves));
        prediccion.setFechaCalculo(hoy);
        prediccionDAO.insertar(prediccion);
        return prediccion;
    }

    /**
     * Calcula el riesgo de todos los estudiantes.
     *
     * @param hoy fecha del cálculo
     * @return cantidad de estudiantes por nivel (Alto, Medio, Bajo)
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Map<String, Integer> calcularTodos(LocalDate hoy) throws SQLException {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        conteo.put(ALTO, 0);
        conteo.put(MEDIO, 0);
        conteo.put(BAJO, 0);
        for (Estudiante e : estudianteDAO.listarTodos()) {
            String nivel = calcular(e.getIdEstudiante(), hoy).getNivelRiesgo();
            conteo.merge(nivel, 1, Integer::sum);
        }
        return conteo;
    }

    /**
     * Nivel de riesgo según la cantidad de señales de alerta.
     *
     * @param faltas          faltas sin justificar en los últimos 30 días
     * @param competenciasEnC competencias cuya calificación más reciente es C
     * @param graves          incidencias graves en los últimos 60 días
     * @return Alto, Medio o Bajo
     */
    public String nivel(int faltas, int competenciasEnC, int graves) {
        int senales = 0;
        if (faltas >= FALTAS_PARA_ALERTA) {
            senales++;
        }
        if (competenciasEnC > 0) {
            senales++;
        }
        if (graves > 0) {
            senales++;
        }
        if (senales >= 2) {
            return ALTO;
        }
        return senales == 1 ? MEDIO : BAJO;
    }

    /**
     * Texto que explica el resultado (se muestra en el historial).
     *
     * @param faltas          faltas sin justificar en los últimos 30 días
     * @param competenciasEnC competencias en C
     * @param graves          incidencias graves en los últimos 60 días
     * @return motivo del nivel de riesgo
     */
    public String motivo(int faltas, int competenciasEnC, int graves) {
        List<String> partes = new ArrayList<>();
        if (faltas >= FALTAS_PARA_ALERTA) {
            partes.add(faltas + " faltas sin justificar en los últimos " + DIAS_ASISTENCIA + " días");
        }
        if (competenciasEnC > 0) {
            partes.add(competenciasEnC + (competenciasEnC == 1 ? " competencia" : " competencias") + " en nivel C");
        }
        if (graves > 0) {
            partes.add(graves + (graves == 1 ? " incidencia grave" : " incidencias graves")
                    + " en los últimos " + DIAS_INCIDENCIAS + " días");
        }
        if (partes.isEmpty()) {
            return "Sin alertas de asistencia, rendimiento ni conducta.";
        }
        return String.join("; ", partes) + ".";
    }

    /**
     * @param registros asistencia del estudiante
     * @param hoy       fecha de referencia
     * @return faltas sin justificación en los últimos 30 días (incluye hoy)
     */
    int faltasSinJustificar(List<Asistencia> registros, LocalDate hoy) {
        LocalDate desde = hoy.minusDays(DIAS_ASISTENCIA - 1L);
        return (int) registros.stream()
                .filter(a -> "Falta".equals(a.getEstado()) && a.getIdJustificacion() == null)
                .filter(a -> a.getFecha() != null && !a.getFecha().isBefore(desde) && !a.getFecha().isAfter(hoy))
                .count();
    }

    /**
     * @param calificaciones calificaciones del estudiante
     * @return competencias cuya calificación más reciente (último periodo y mes) es C
     */
    int competenciasEnC(List<Calificacion> calificaciones) {
        Map<Integer, Calificacion> ultima = new HashMap<>();
        Comparator<Calificacion> orden = Comparator.comparingInt(Calificacion::getIdPeriodo)
                .thenComparingInt(Calificacion::getMes);
        for (Calificacion c : calificaciones) {
            ultima.merge(c.getIdCompetencia(), c, (a, b) -> orden.compare(a, b) >= 0 ? a : b);
        }
        return (int) ultima.values().stream().filter(c -> "C".equals(c.getNivelMensual())).count();
    }

    /**
     * @param incidencias incidencias del estudiante
     * @param hoy         fecha de referencia
     * @return incidencias graves de los últimos 60 días
     */
    int incidenciasGraves(List<Incidencia> incidencias, LocalDate hoy) {
        LocalDate desde = hoy.minusDays(DIAS_INCIDENCIAS - 1L);
        return (int) incidencias.stream()
                .filter(i -> "Grave".equals(i.getGravedad()))
                .filter(i -> i.getFechaHora() != null && !i.getFechaHora().toLocalDate().isBefore(desde))
                .count();
    }
}