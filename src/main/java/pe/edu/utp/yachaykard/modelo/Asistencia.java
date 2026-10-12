package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Registro diario de asistencia de un estudiante, tomado por el auxiliar.
 * Estados de SIAGIE: Asistió, Tardanza, Faltó (justificada) y Falta injustificada.
 */
public class Asistencia implements Serializable {

    private int idAsistencia;
    private LocalDate fecha;
    private String estado;
    private int idEstudiante;
    private int idAuxiliar;
    private Integer idJustificacion;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Asistencia() {
    }

    /**
     * Crea un objeto Asistencia con todos sus datos.
     *
     * @param idAsistencia    identificador del registro
     * @param fecha           fecha de la asistencia
     * @param estado          Asistió, Tardanza, Faltó o Falta injustificada
     * @param idEstudiante    estudiante
     * @param idAuxiliar      auxiliar que registra
     * @param idJustificacion justificación asociada; null si no tiene
     */
    public Asistencia(int idAsistencia, LocalDate fecha, String estado, int idEstudiante, int idAuxiliar, Integer idJustificacion) {
        this.idAsistencia = idAsistencia;
        this.fecha = fecha;
        this.estado = estado;
        this.idEstudiante = idEstudiante;
        this.idAuxiliar = idAuxiliar;
        this.idJustificacion = idJustificacion;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdAuxiliar() {
        return idAuxiliar;
    }

    public void setIdAuxiliar(int idAuxiliar) {
        this.idAuxiliar = idAuxiliar;
    }

    public Integer getIdJustificacion() {
        return idJustificacion;
    }

    public void setIdJustificacion(Integer idJustificacion) {
        this.idJustificacion = idJustificacion;
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "idAsistencia=" + idAsistencia +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", idEstudiante=" + idEstudiante +
                ", idAuxiliar=" + idAuxiliar +
                ", idJustificacion=" + idJustificacion +
                '}';
    }
}