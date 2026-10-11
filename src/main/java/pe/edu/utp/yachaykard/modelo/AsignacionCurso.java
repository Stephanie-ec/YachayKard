package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/*
Asignación de un docente a un curso en una sección para un año académico.*/
public class AsignacionCurso implements Serializable {

    private int idAsignacion;
    private String anioAcademico;
    private int idDocente;
    private int idCurso;
    private int idSeccion;

    /* Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public AsignacionCurso() {
    }

    /**
     * Crea un objeto AsignacionCurso con todos sus datos.
     *
     * @param idAsignacion  identificador de la asignación
     * @param anioAcademico año académico
     * @param idDocente     docente asignado
     * @param idCurso       curso que dicta
     * @param idSeccion     sección donde lo dicta
     */
    public AsignacionCurso(int idAsignacion, String anioAcademico, int idDocente, int idCurso, int idSeccion) {
        this.idAsignacion = idAsignacion;
        this.anioAcademico = anioAcademico;
        this.idDocente = idDocente;
        this.idCurso = idCurso;
        this.idSeccion = idSeccion;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public String getAnioAcademico() {
        return anioAcademico;
    }

    public void setAnioAcademico(String anioAcademico) {
        this.anioAcademico = anioAcademico;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    @Override
    public String toString() {
        return "AsignacionCurso{" +
                "idAsignacion=" + idAsignacion +
                ", anioAcademico=" + anioAcademico +
                ", idDocente=" + idDocente +
                ", idCurso=" + idCurso +
                ", idSeccion=" + idSeccion +
                '}';
    }
}