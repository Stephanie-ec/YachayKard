package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Incidencia de conducta registrada por un docente (en clase) o por el auxiliar (fuera del aula).
 * Solo admite medidas formativas internas; no existe la suspensión.
 */
public class Incidencia implements Serializable {

    private int idIncidencia;
    private LocalDateTime fechaHora;
    private String descripcion;
    private int idEstudiante;
    private int idTipoIncidencia;
    private String gravedad;
    private String accionTomada;
    private int idUsuario;
    private Integer idCurso;
    private boolean requiereSiseve;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Incidencia() {
    }

    /**
     * Crea un objeto Incidencia con todos sus datos.
     *
     * @param idIncidencia     identificador de la incidencia
     * @param fechaHora        fecha y hora del registro
     * @param descripcion      descripción (obligatoria si es Grave u Otra)
     * @param idEstudiante     estudiante involucrado
     * @param idTipoIncidencia tipo de falta del catálogo
     * @param gravedad         Leve, Moderada o Grave
     * @param accionTomada     medida formativa aplicada
     * @param idUsuario        usuario que registra (docente o auxiliar)
     * @param idCurso          curso; null si ocurrió fuera del aula
     * @param requiereSiseve   true si es un caso de violencia que requiere reporte en SíseVe
     */
    public Incidencia(int idIncidencia, LocalDateTime fechaHora, String descripcion, int idEstudiante, int idTipoIncidencia, String gravedad, String accionTomada, int idUsuario, Integer idCurso, boolean requiereSiseve) {
        this.idIncidencia = idIncidencia;
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
        this.idEstudiante = idEstudiante;
        this.idTipoIncidencia = idTipoIncidencia;
        this.gravedad = gravedad;
        this.accionTomada = accionTomada;
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
        this.requiereSiseve = requiereSiseve;
    }

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdTipoIncidencia() {
        return idTipoIncidencia;
    }

    public void setIdTipoIncidencia(int idTipoIncidencia) {
        this.idTipoIncidencia = idTipoIncidencia;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public String getAccionTomada() {
        return accionTomada;
    }

    public void setAccionTomada(String accionTomada) {
        this.accionTomada = accionTomada;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public boolean isRequiereSiseve() {
        return requiereSiseve;
    }

    public void setRequiereSiseve(boolean requiereSiseve) {
        this.requiereSiseve = requiereSiseve;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "idIncidencia=" + idIncidencia +
                ", fechaHora=" + fechaHora +
                ", descripcion=" + descripcion +
                ", idEstudiante=" + idEstudiante +
                ", idTipoIncidencia=" + idTipoIncidencia +
                ", gravedad=" + gravedad +
                ", accionTomada=" + accionTomada +
                ", idUsuario=" + idUsuario +
                ", idCurso=" + idCurso +
                ", requiereSiseve=" + requiereSiseve +
                '}';
    }
}