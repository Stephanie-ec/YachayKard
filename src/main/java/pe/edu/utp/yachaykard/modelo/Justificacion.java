package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Justificación de inasistencia. La de 1 día la registra el auxiliar;
 * la de 2 o más días, el administrador (presentada por mesa de partes).
 */
public class Justificacion implements Serializable {

    private int idJustificacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
    private String evidencia;
    private int idEstudiante;
    private int idUsuario;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Justificacion() {
    }

    /**
     * Crea un objeto Justificacion con todos sus datos.
     *
     * @param idJustificacion identificador de la justificación
     * @param fechaInicio     primer día justificado
     * @param fechaFin        último día justificado
     * @param motivo          motivo de la inasistencia
     * @param evidencia       evidencia presentada (documento, constancia)
     * @param idEstudiante    estudiante justificado
     * @param idUsuario       usuario que registra la justificación
     */
    public Justificacion(int idJustificacion, LocalDate fechaInicio, LocalDate fechaFin, String motivo, String evidencia, int idEstudiante, int idUsuario) {
        this.idJustificacion = idJustificacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
        this.evidencia = evidencia;
        this.idEstudiante = idEstudiante;
        this.idUsuario = idUsuario;
    }

    public int getIdJustificacion() {
        return idJustificacion;
    }

    public void setIdJustificacion(int idJustificacion) {
        this.idJustificacion = idJustificacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Justificacion{" +
                "idJustificacion=" + idJustificacion +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", motivo=" + motivo +
                ", evidencia=" + evidencia +
                ", idEstudiante=" + idEstudiante +
                ", idUsuario=" + idUsuario +
                '}';
    }
}