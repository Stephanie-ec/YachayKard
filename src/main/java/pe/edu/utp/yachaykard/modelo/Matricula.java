package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Matrícula de un estudiante en una sección para un año lectivo.
 */
public class Matricula implements Serializable {

    private int idMatricula;
    private LocalDate fechaMatricula;
    private String anioLectivo;
    private String estado;
    private int idEstudiante;
    private int idSeccion;

    /**
     * Constructor vacío requerido por JSF y por el DAO al mapear filas.
     */
    public Matricula() {
    }

    /**
     * Crea un objeto Matricula con todos sus datos.
     *
     * @param idMatricula    identificador de la matrícula
     * @param fechaMatricula fecha de matrícula
     * @param anioLectivo    año lectivo (por ejemplo, 2026)
     * @param estado         estado de la matrícula (Activa, Retirado, Trasladado)
     * @param idEstudiante   estudiante matriculado
     * @param idSeccion      sección asignada
     */
    public Matricula(int idMatricula, LocalDate fechaMatricula, String anioLectivo, String estado, int idEstudiante, int idSeccion) {
        this.idMatricula = idMatricula;
        this.fechaMatricula = fechaMatricula;
        this.anioLectivo = anioLectivo;
        this.estado = estado;
        this.idEstudiante = idEstudiante;
        this.idSeccion = idSeccion;
    }

    public int getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(int idMatricula) {
        this.idMatricula = idMatricula;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(String anioLectivo) {
        this.anioLectivo = anioLectivo;
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

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    @Override
    public String toString() {
        return "Matricula{" +
                "idMatricula=" + idMatricula +
                ", fechaMatricula=" + fechaMatricula +
                ", anioLectivo=" + anioLectivo +
                ", estado=" + estado +
                ", idEstudiante=" + idEstudiante +
                ", idSeccion=" + idSeccion +
                '}';
    }
}
