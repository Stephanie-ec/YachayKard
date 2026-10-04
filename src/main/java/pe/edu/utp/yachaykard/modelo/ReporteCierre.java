package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class ReporteCierre implements Serializable {
    private int idReporte;
    private LocalDate fechaGeneracion;
    private int idEstudiante;
    private int idPeriodo;

    public ReporteCierre() {}

    public ReporteCierre(int idReporte, LocalDate fechaGeneracion, int idEstudiante, int idPeriodo) {
        this.idReporte = idReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.idEstudiante = idEstudiante;
        this.idPeriodo = idPeriodo;
    }

    public int getIdReporte() { return idReporte; }
    public void setIdReporte(int idReporte) { this.idReporte = idReporte; }
    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public int getIdPeriodo() { return idPeriodo; }
    public void setIdPeriodo(int idPeriodo) { this.idPeriodo = idPeriodo; }

    @Override
    public String toString() {
        return "ReporteCierre{idReporte=" + idReporte + ", fechaGeneracion=" + fechaGeneracion + "}";
    }
}