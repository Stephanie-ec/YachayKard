package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Incidencia implements Serializable {
    private int idIncidencia;
    private LocalDate fecha;
    private String descripcion;
    private int idEstudiante;

    public Incidencia() {}

    public Incidencia(int idIncidencia, LocalDate fecha, String descripcion, int idEstudiante) {
        this.idIncidencia = idIncidencia;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.idEstudiante = idEstudiante;
    }

    public int getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(int idIncidencia) { this.idIncidencia = idIncidencia; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    @Override
    public String toString() {
        return "Incidencia{idIncidencia=" + idIncidencia + ", fecha=" + fecha + "}";
    }
}