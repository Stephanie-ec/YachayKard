package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Matricula implements Serializable {
    private int idMatricula;
    private LocalDate fechaMatricula;
    private String anioLectivo;
    private int idEstudiante;
    private int idSeccion;

    public Matricula() {}

    public Matricula(int idMatricula, LocalDate fechaMatricula, String anioLectivo,
                     int idEstudiante, int idSeccion) {
        this.idMatricula = idMatricula;
        this.fechaMatricula = fechaMatricula;
        this.anioLectivo = anioLectivo;
        this.idEstudiante = idEstudiante;
        this.idSeccion = idSeccion;
    }

    public int getIdMatricula() { return idMatricula; }
    public void setIdMatricula(int idMatricula) { this.idMatricula = idMatricula; }
    public LocalDate getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(LocalDate fechaMatricula) { this.fechaMatricula = fechaMatricula; }
    public String getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(String anioLectivo) { this.anioLectivo = anioLectivo; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public int getIdSeccion() { return idSeccion; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }

    @Override
    public String toString() {
        return "Matricula{idMatricula=" + idMatricula + ", anioLectivo='" + anioLectivo + "'}";
    }
}