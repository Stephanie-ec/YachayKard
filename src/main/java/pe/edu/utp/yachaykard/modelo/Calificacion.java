package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Calificacion implements Serializable {
    private int idCalificacion;
    private String notaCualitativa;
    private String observacion;
    private int idEstudiante;
    private int idCompetencia;
    private int idPeriodo;

    public Calificacion() {}

    public Calificacion(int idCalificacion, String notaCualitativa, String observacion,
                        int idEstudiante, int idCompetencia, int idPeriodo) {
        this.idCalificacion = idCalificacion;
        this.notaCualitativa = notaCualitativa;
        this.observacion = observacion;
        this.idEstudiante = idEstudiante;
        this.idCompetencia = idCompetencia;
        this.idPeriodo = idPeriodo;
    }

    public int getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(int idCalificacion) { this.idCalificacion = idCalificacion; }
    public String getNotaCualitativa() { return notaCualitativa; }
    public void setNotaCualitativa(String notaCualitativa) { this.notaCualitativa = notaCualitativa; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public int getIdCompetencia() { return idCompetencia; }
    public void setIdCompetencia(int idCompetencia) { this.idCompetencia = idCompetencia; }
    public int getIdPeriodo() { return idPeriodo; }
    public void setIdPeriodo(int idPeriodo) { this.idPeriodo = idPeriodo; }

    @Override
    public String toString() {
        return "Calificacion{idCalificacion=" + idCalificacion + ", notaCualitativa='" + notaCualitativa + "'}";
    }
}