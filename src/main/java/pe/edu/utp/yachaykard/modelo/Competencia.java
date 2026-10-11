package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/*
Competencia del Currículo Nacional que pertenece a un curso o área.*/
public class Competencia implements Serializable {

    private int idCompetencia;
    private String descripcion;
    private int idCurso;
    private String codigo;

    /* Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Competencia() {
    }

    /**
     * Crea un objeto Competencia con todos sus datos.
     *
     * @param idCompetencia identificador de la competencia
     * @param descripcion   descripción de la competencia
     * @param idCurso       curso o área al que pertenece
     * @param codigo        código de la competencia dentro del área
     */
    public Competencia(int idCompetencia, String descripcion, int idCurso, String codigo) {
        this.idCompetencia = idCompetencia;
        this.descripcion = descripcion;
        this.idCurso = idCurso;
        this.codigo = codigo;
    }

    public int getIdCompetencia() {
        return idCompetencia;
    }

    public void setIdCompetencia(int idCompetencia) {
        this.idCompetencia = idCompetencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return "Competencia{" +
                "idCompetencia=" + idCompetencia +
                ", descripcion=" + descripcion +
                ", idCurso=" + idCurso +
                ", codigo=" + codigo +
                '}';
    }

}