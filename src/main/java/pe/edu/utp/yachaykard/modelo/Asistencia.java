package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Asistencia implements Serializable {
    private int idAsistencia;
    private LocalDate fecha;
    private String estado;
    private int idEstudiante;

    public Asistencia() {}

    public Asistencia(int idAsistencia, LocalDate fecha, String estado, int idEstudiante) {
        this.idAsistencia = idAsistencia;
        this.fecha = fecha;
        this.estado = estado;
        this.idEstudiante = idEstudiante;
    }

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    @Override
    public String toString() {
        return "Asistencia{idAsistencia=" + idAsistencia + ", fecha=" + fecha + ", estado='" + estado + "'}";
    }
}