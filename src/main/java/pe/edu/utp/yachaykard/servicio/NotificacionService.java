package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.DestinatariosDAO;
import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.dao.NotificacionDAO;
import pe.edu.utp.yachaykard.dao.PrediccionRiesgoDAO;
import pe.edu.utp.yachaykard.dao.impl.AsistenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DestinatariosDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.NotificacionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PrediccionRiesgoDAOImpl;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.Notificacion;
import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Alertas internas (RF-27). Por cada estudiante con matrícula activa revisa tres situaciones
 * y avisa dentro de la plataforma al tutor de su sección y a los administradores:
 * <ul>
 *     <li>Semana de faltas: 5 o más faltas sin justificar en los últimos 7 días.</li>
 *     <li>Incidencia SíseVe: una incidencia marcada para el SíseVe en los últimos 7 días.</li>
 *     <li>Riesgo alto: la última predicción de riesgo del estudiante es Alto.</li>
 * </ul>
 * La misma alerta no se repite al mismo usuario durante 7 días. Las alertas se leen y se atienden
 * en la Bandeja del tutor (bandeja.xhtml).
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class NotificacionService {

    public static final String CANAL_INTERNA = "Interna";
    public static final String NO_LEIDA = "No leída";
    public static final String TIPO_FALTAS = "Semana de faltas";
    public static final String TIPO_SISEVE = "Incidencia SíseVe";
    public static final String TIPO_RIESGO = "Riesgo alto";
    static final int FALTAS_SEMANA = 5;
    static final int DIAS_REVISION = 7;

    private final DestinatariosDAO destinatariosDAO;
    private final AsistenciaDAO asistenciaDAO;
    private final IncidenciaDAO incidenciaDAO;
    private final PrediccionRiesgoDAO prediccionDAO;
    private final NotificacionDAO notificacionDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public NotificacionService() {
        this(new DestinatariosDAOImpl(), new AsistenciaDAOImpl(), new IncidenciaDAOImpl(),
                new PrediccionRiesgoDAOImpl(), new NotificacionDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param destinatariosDAO administradores y tutor de cada estudiante
     * @param asistenciaDAO    acceso a asistencia
     * @param incidenciaDAO    acceso a incidencias
     * @param prediccionDAO    acceso a predicciones de riesgo
     * @param notificacionDAO  acceso a notificaciones
     */
    public NotificacionService(DestinatariosDAO destinatariosDAO, AsistenciaDAO asistenciaDAO,
                               IncidenciaDAO incidenciaDAO, PrediccionRiesgoDAO prediccionDAO,
                               NotificacionDAO notificacionDAO) {
        this.destinatariosDAO = destinatariosDAO;
        this.asistenciaDAO = asistenciaDAO;
        this.incidenciaDAO = incidenciaDAO;
        this.prediccionDAO = prediccionDAO;
        this.notificacionDAO = notificacionDAO;
    }

    /**
     * Revisa a todos los estudiantes y registra las alertas nuevas.
     *
     * @param ahora fecha y hora de Lima
     * @return cantidad de alertas registradas
     * @throws SQLException si ocurre un error en la base de datos
     */
    public int generar(LocalDateTime ahora) throws SQLException {
        List<Integer> administradores = destinatariosDAO.listarAdministradores();
        LocalDateTime desde = ahora.minusDays(DIAS_REVISION);
        int registradas = 0;
        for (DestinatariosDAO.Destino destino : destinatariosDAO.listarDestinos(String.valueOf(ahora.getYear()))) {
            Map<String, String> alertas = alertasDe(destino, ahora.toLocalDate());
            if (alertas.isEmpty()) {
                continue;
            }
            Set<Integer> usuarios = new LinkedHashSet<>(administradores);
            if (destino.idUsuarioTutor() != null) {
                usuarios.add(destino.idUsuarioTutor());
            }
            for (Map.Entry<String, String> alerta : alertas.entrySet()) {
                for (int idUsuario : usuarios) {
                    if (!notificacionDAO.existeAlerta(destino.idEstudiante(), idUsuario, alerta.getKey(), desde)) {
                        notificacionDAO.insertar(nueva(destino.idEstudiante(), idUsuario, alerta.getKey(),
                                alerta.getValue(), ahora));
                        registradas++;
                    }
                }
            }
        }
        return registradas;
    }

    /**
     * @param destino estudiante a revisar
     * @param hoy     fecha de referencia
     * @return tipo de alerta y su mensaje (vacío si no hay alertas)
     * @throws SQLException si ocurre un error en la base de datos
     */
    Map<String, String> alertasDe(DestinatariosDAO.Destino destino, LocalDate hoy) throws SQLException {
        Map<String, String> alertas = new LinkedHashMap<>();
        int faltas = faltasDeLaSemana(asistenciaDAO.listarPorEstudiante(destino.idEstudiante()), hoy);
        if (faltas >= FALTAS_SEMANA) {
            alertas.put(TIPO_FALTAS, destino.estudiante() + ": " + faltas
                    + " faltas sin justificar en los últimos " + DIAS_REVISION + " días.");
        }
        int siseve = incidenciasSiseve(incidenciaDAO.listarPorEstudiante(destino.idEstudiante()), hoy);
        if (siseve > 0) {
            alertas.put(TIPO_SISEVE, destino.estudiante() + ": " + siseve
                    + (siseve == 1 ? " incidencia" : " incidencias") + " para reportar en el SíseVe.");
        }
        PrediccionRiesgo prediccion = prediccionDAO.buscarUltimaPorEstudiante(destino.idEstudiante());
        if (prediccion != null && PrediccionRiesgoService.ALTO.equals(prediccion.getNivelRiesgo())) {
            alertas.put(TIPO_RIESGO, destino.estudiante() + ": riesgo alto de deserción. " + prediccion.getMotivo());
        }
        return alertas;
    }

    /**
     * @param registros asistencia del estudiante
     * @param hoy       fecha de referencia
     * @return faltas sin justificar de los últimos 7 días (incluye hoy)
     */
    int faltasDeLaSemana(List<Asistencia> registros, LocalDate hoy) {
        LocalDate desde = hoy.minusDays(DIAS_REVISION - 1L);
        return (int) registros.stream()
                .filter(a -> "Falta".equals(a.getEstado()) && a.getIdJustificacion() == null)
                .filter(a -> a.getFecha() != null && !a.getFecha().isBefore(desde) && !a.getFecha().isAfter(hoy))
                .count();
    }

    /**
     * @param incidencias incidencias del estudiante
     * @param hoy         fecha de referencia
     * @return incidencias para el SíseVe de los últimos 7 días
     */
    int incidenciasSiseve(List<Incidencia> incidencias, LocalDate hoy) {
        LocalDate desde = hoy.minusDays(DIAS_REVISION - 1L);
        return (int) incidencias.stream()
                .filter(Incidencia::isRequiereSiseve)
                .filter(i -> i.getFechaHora() != null && !i.getFechaHora().toLocalDate().isBefore(desde))
                .count();
    }

    /**
     * @param idUsuario usuario con sesión
     * @return alertas internas sin leer del usuario, de la más reciente a la más antigua
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Notificacion> pendientes(int idUsuario) throws SQLException {
        return notificacionDAO.listarPorUsuario(idUsuario).stream()
                .filter(n -> CANAL_INTERNA.equals(n.getCanal()) && NO_LEIDA.equals(n.getEstado()))
                .toList();
    }

    private static Notificacion nueva(int idEstudiante, int idUsuario, String tipo, String mensaje,
                                      LocalDateTime ahora) {
        Notificacion n = new Notificacion();
        n.setFechaEnvio(ahora);
        n.setMensaje(mensaje);
        n.setIdUsuario(idUsuario);
        n.setIdEstudiante(idEstudiante);
        n.setCanal(CANAL_INTERNA);
        n.setTipo(tipo);
        n.setEstado(NO_LEIDA);
        return n;
    }
}