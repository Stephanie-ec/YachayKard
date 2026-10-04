package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Competencia implements Serializable {
    private int idCompetencia;
    private String descripcion;
    private int idCurso;

    public Competencia() {}

    public Competencia(int idCompetencia, String descripcion, int idCurso) {
        this.idCompetencia = idCompetencia;
        this.descripcion = descripcion;
        this.idCurso = idCurso;
    }

    public int getIdCompetencia() { return idCompetencia; }
    public void setIdCompetencia(int idCompetencia) { this.idCompetencia = idCompetencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    @Override
    public String toString() {
        return "Competencia{idCompetencia=" + idCompetencia + ", descripcion='" + descripcion + "'}";
    }
}