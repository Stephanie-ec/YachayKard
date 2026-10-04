package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Curso implements Serializable {
    private int idCurso;
    private String nombreCurso;
    private int horasSemanales;

    public Curso() {}

    public Curso(int idCurso, String nombreCurso, int horasSemanales) {
        this.idCurso = idCurso;
        this.nombreCurso = nombreCurso;
        this.horasSemanales = horasSemanales;
    }

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public int getHorasSemanales() { return horasSemanales; }
    public void setHorasSemanales(int horasSemanales) { this.horasSemanales = horasSemanales; }

    @Override
    public String toString() {
        return "Curso{idCurso=" + idCurso + ", nombreCurso='" + nombreCurso + "'}";
    }
}