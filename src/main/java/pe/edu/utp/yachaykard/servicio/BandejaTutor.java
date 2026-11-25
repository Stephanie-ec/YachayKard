package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.NotificacionDAO;
import pe.edu.utp.yachaykard.dao.impl.NotificacionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Apoderado;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.Notificacion;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reglas de la Bandeja del tutor (RF-24) y de los avisos por WhatsApp (RF-28):
 * <ul>
 *     <li>Las incidencias Moderadas y Graves se avisan al apoderado; su estado pasa de
 *         "Pendiente de avisar" a "Enviado".</li>
 *     <li>Las alertas internas de faltas y de riesgo alto también se avisan; al avisar quedan "Atendida".</li>
 *     <li>Cada aviso se guarda en la tabla notificacion con canal WhatsApp.</li>
 * </ul>
 * Los textos de canal, tipo y estado son los mismos que usa NotificacionService.
 * El DAO se recibe en el constructor para poder simularlo con Mockito en las pruebas.
 */
public class BandejaTutor {

    public static final String CANAL_INTERNA = "Interna";
    public static final String CANAL_WHATSAPP = "WhatsApp";
    public static final String NO_LEIDA = "No leída";
    public static final String LEIDA = "Leída";
    public static final String ATENDIDA = "Atendida";
    public static final String ENVIADO = "Enviado";
    public static final String PENDIENTE = "Pendiente de avisar";
    public static final String SIN_AVISO = "—";
    static final Set<String> GRAVEDADES_CON_AVISO = Set.of("Moderada", "Grave");
    static final Set<String> ALERTAS_CON_AVISO = Set.of(AvisoWhatsApp.TIPO_FALTAS, AvisoWhatsApp.TIPO_RIESGO);

    private final NotificacionDAO notificacionDAO;
    private final AvisoWhatsApp whatsApp = new AvisoWhatsApp();

    /**
     * Constructor que usa la aplicación: trabaja con la base de datos.
     */
    public BandejaTutor() {
        this(new NotificacionDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param notificacionDAO acceso a notificaciones
     */
    public BandejaTutor(NotificacionDAO notificacionDAO) {
        this.notificacionDAO = notificacionDAO;
    }

    /**
     * @param incidencia incidencia registrada
     * @return true si es Moderada o Grave
     */
    public static boolean requiereAviso(Incidencia incidencia) {
        return incidencia.getGravedad() != null && GRAVEDADES_CON_AVISO.contains(incidencia.getGravedad());
    }

    /**
     * @param alerta alerta interna
     * @return true si es de faltas o de riesgo alto y aún no se avisó
     */
    public static boolean alertaAvisable(Notificacion alerta) {
        return alerta.getTipo() != null && ALERTAS_CON_AVISO.contains(alerta.getTipo())
                && !ATENDIDA.equals(alerta.getEstado());
    }

    /**
     * @param incidencia incidencia registrada
     * @return tipo con el que se guarda su aviso (cabe en los 30 caracteres de la columna tipo)
     */
    static String tipoAviso(Incidencia incidencia) {
        return "Aviso incidencia " + incidencia.getIdIncidencia();
    }

    /**
     * @return tipos de los avisos de WhatsApp ya enviados
     * @throws SQLException si ocurre un error en la base de datos
     */
    public Set<String> avisosEnviados() throws SQLException {
        return notificacionDAO.listarTodos().stream()
                .filter(n -> CANAL_WHATSAPP.equals(n.getCanal()))
                .map(Notificacion::getTipo)
                .collect(Collectors.toSet());
    }

    /**
     * @param incidencia incidencia registrada
     * @param enviados   tipos de los avisos ya enviados
     * @return "—", "Pendiente de avisar" o "Enviado"
     */
    public String estadoAviso(Incidencia incidencia, Set<String> enviados) {
        if (!requiereAviso(incidencia)) {
            return SIN_AVISO;
        }
        return enviados.contains(tipoAviso(incidencia)) ? ENVIADO : PENDIENTE;
    }

    /**
     * Registra el aviso de una incidencia y devuelve el enlace de WhatsApp.
     *
     * @param incidencia incidencia Moderada o Grave
     * @param apoderado  apoderado del estudiante
     * @param estudiante nombre del estudiante
     * @param colegio    nombre de la institución
     * @param nombreTipo nombre del tipo de incidencia
     * @param idUsuario  usuario que avisa
     * @param ahora      fecha y hora de Lima
     * @return enlace wa.me con el mensaje
     * @throws IllegalArgumentException si la incidencia es Leve o el celular no es válido
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public String avisarIncidencia(Incidencia incidencia, Apoderado apoderado, String estudiante, String colegio,
                                   String nombreTipo, int idUsuario, LocalDateTime ahora) throws SQLException {
        if (!requiereAviso(incidencia)) {
            throw new IllegalArgumentException("Solo se avisan las incidencias Moderadas y Graves.");
        }
        String mensaje = whatsApp.mensajeIncidencia(apoderado.getNombres(), estudiante, colegio, nombreTipo,
                incidencia.getGravedad(), incidencia.getFechaHora(), incidencia.getAccionTomada());
        String enlace = whatsApp.enlace(apoderado.getTelefono(), mensaje);
        notificacionDAO.insertar(aviso(apoderado, idUsuario, incidencia.getIdEstudiante(),
                tipoAviso(incidencia), mensaje, ahora));
        return enlace;
    }

    /**
     * Registra el aviso de una alerta de faltas o de riesgo y la deja "Atendida".
     *
     * @param alerta     alerta interna del usuario
     * @param apoderado  apoderado del estudiante
     * @param estudiante nombre del estudiante
     * @param colegio    nombre de la institución
     * @param idUsuario  usuario que avisa
     * @param ahora      fecha y hora de Lima
     * @return enlace wa.me con el mensaje
     * @throws IllegalArgumentException si la alerta no se avisa o el celular no es válido
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public String avisarAlerta(Notificacion alerta, Apoderado apoderado, String estudiante, String colegio,
                               int idUsuario, LocalDateTime ahora) throws SQLException {
        if (!alertaAvisable(alerta)) {
            throw new IllegalArgumentException("Esta alerta no se avisa al apoderado o ya fue atendida.");
        }
        String mensaje = whatsApp.mensajeAlerta(apoderado.getNombres(), estudiante, colegio, alerta.getTipo());
        String enlace = whatsApp.enlace(apoderado.getTelefono(), mensaje);
        notificacionDAO.insertar(aviso(apoderado, idUsuario, alerta.getIdEstudiante(),
                "Aviso " + alerta.getTipo(), mensaje, ahora));
        alerta.setEstado(ATENDIDA);
        notificacionDAO.actualizar(alerta);
        return enlace;
    }

    /**
     * Marca una alerta como leída (si no estaba leída ni atendida).
     *
     * @param alerta alerta interna
     * @throws SQLException si ocurre un error en la base de datos
     */
    public void marcarLeida(Notificacion alerta) throws SQLException {
        if (NO_LEIDA.equals(alerta.getEstado())) {
            alerta.setEstado(LEIDA);
            notificacionDAO.actualizar(alerta);
        }
    }

    /**
     * @param idUsuario usuario con sesión
     * @return alertas internas del usuario, de la más reciente a la más antigua
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Notificacion> alertas(int idUsuario) throws SQLException {
        return notificacionDAO.listarPorUsuario(idUsuario).stream()
                .filter(n -> CANAL_INTERNA.equals(n.getCanal()))
                .toList();
    }

    private static Notificacion aviso(Apoderado apoderado, int idUsuario, int idEstudiante, String tipo,
                                      String mensaje, LocalDateTime ahora) {
        Notificacion n = new Notificacion();
        n.setFechaEnvio(ahora);
        n.setMensaje(mensaje);
        n.setIdApoderado(apoderado.getIdApoderado());
        n.setIdUsuario(idUsuario);
        n.setIdEstudiante(idEstudiante);
        n.setCanal(CANAL_WHATSAPP);
        n.setTipo(tipo);
        n.setEstado(ENVIADO);
        return n;
    }
}
