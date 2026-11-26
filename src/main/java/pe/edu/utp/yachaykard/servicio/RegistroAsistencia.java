package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.JustificacionDAO;
import pe.edu.utp.yachaykard.dao.impl.AsistenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.JustificacionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Justificacion;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Registro diario de asistencia (RF-18) y justificación de faltas de un día (RF-19):
 * <ul>
 *     <li>No se registra asistencia de fechas futuras ni de sábados o domingos.</li>
 *     <li>Los estados son Presente, Tardanza o Falta.</li>
 *     <li>Solo se justifica una falta ya guardada, con un motivo de 5 a 200 caracteres.</li>
 *     <li>Si una falta justificada se corrige a Presente o Tardanza, deja de estar justificada.</li>
 * </ul>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class RegistroAsistencia {

    public static final String PRESENTE = "Presente";
    public static final String TARDANZA = "Tardanza";
    public static final String FALTA = "Falta";
    static final Set<String> ESTADOS = Set.of(PRESENTE, TARDANZA, FALTA);
    static final int MOTIVO_MINIMO = 5;
    static final int MOTIVO_MAXIMO = 200;

    private final AsistenciaDAO asistenciaDAO;
    private final JustificacionDAO justificacionDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public RegistroAsistencia() {
        this(new AsistenciaDAOImpl(), new JustificacionDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param asistenciaDAO    acceso a asistencia
     * @param justificacionDAO acceso a justificaciones
     */
    public RegistroAsistencia(AsistenciaDAO asistenciaDAO, JustificacionDAO justificacionDAO) {
        this.asistenciaDAO = asistenciaDAO;
        this.justificacionDAO = justificacionDAO;
    }

    /**
     * Revisa que se pueda registrar asistencia en la fecha.
     *
     * @param fecha día de la asistencia
     * @param hoy   fecha actual (en Lima)
     * @throws IllegalArgumentException si la fecha es futura o cae en fin de semana
     */
    public void validarFecha(LocalDate fecha, LocalDate hoy) {
        if (fecha == null) {
            throw new IllegalArgumentException("Elija la fecha.");
        }
        if (fecha.isAfter(hoy)) {
            throw new IllegalArgumentException("No se puede registrar asistencia de una fecha futura.");
        }
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No hay clases los sábados ni domingos.");
        }
    }

    /**
     * Guarda la asistencia de un día: inserta los registros nuevos y actualiza los que ya existían.
     *
     * @param registros  un registro por estudiante (id 0 si es nuevo)
     * @param fecha      día de la asistencia
     * @param hoy        fecha actual (en Lima)
     * @param idAuxiliar auxiliar que registra
     * @throws IllegalArgumentException si la fecha o algún estado no es válido
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void guardar(List<Asistencia> registros, LocalDate fecha, LocalDate hoy, int idAuxiliar) throws SQLException {
        validarFecha(fecha, hoy);
        if (idAuxiliar <= 0) {
            throw new IllegalArgumentException("La sección no tiene un auxiliar asignado.");
        }
        for (Asistencia a : registros) {
            if (a.getEstado() == null || !ESTADOS.contains(a.getEstado())) {
                throw new IllegalArgumentException("El estado debe ser Presente, Tardanza o Falta.");
            }
        }
        for (Asistencia a : registros) {
            a.setFecha(fecha);
            a.setIdAuxiliar(idAuxiliar);
            if (!FALTA.equals(a.getEstado())) {
                a.setIdJustificacion(null);
            }
            if (a.getIdAsistencia() == 0) {
                asistenciaDAO.insertar(a);
            } else {
                asistenciaDAO.actualizar(a);
            }
        }
    }

    /**
     * Justifica una falta de un día (RF-19).
     *
     * @param falta     registro de asistencia ya guardado con estado Falta
     * @param motivo    motivo de la justificación
     * @param idUsuario usuario que justifica
     * @throws IllegalStateException    si no es una falta guardada o ya está justificada
     * @throws IllegalArgumentException si el motivo no cumple el largo
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void justificar(Asistencia falta, String motivo, int idUsuario) throws SQLException {
        if (falta.getIdAsistencia() == 0 || !FALTA.equals(falta.getEstado())) {
            throw new IllegalStateException("Solo se justifica una falta que ya está guardada.");
        }
        if (falta.getIdJustificacion() != null) {
            throw new IllegalStateException("Esta falta ya está justificada.");
        }
        String texto = motivo == null ? "" : motivo.trim();
        if (texto.length() < MOTIVO_MINIMO || texto.length() > MOTIVO_MAXIMO) {
            throw new IllegalArgumentException("El motivo debe tener entre " + MOTIVO_MINIMO + " y "
                    + MOTIVO_MAXIMO + " caracteres.");
        }
        Justificacion justificacion = new Justificacion();
        justificacion.setFechaInicio(falta.getFecha());
        justificacion.setFechaFin(falta.getFecha());
        justificacion.setMotivo(texto);
        justificacion.setIdEstudiante(falta.getIdEstudiante());
        justificacion.setIdUsuario(idUsuario);
        int idJustificacion = justificacionDAO.registrar(justificacion);

        falta.setIdJustificacion(idJustificacion);
        asistenciaDAO.actualizar(falta);
    }
}
