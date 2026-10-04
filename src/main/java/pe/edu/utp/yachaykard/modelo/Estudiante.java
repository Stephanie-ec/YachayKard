package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Estudiante implements Serializable {
    private int idEstudiante;
    private String dni;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private int idApoderado;

    public Estudiante() {}

    public Estudiante(int idEstudiante, String dni, String nombres, String apellidos,
                      LocalDate fechaNacimiento, int idApoderado) {
        this.idEstudiante = idEstudiante;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.idApoderado = idApoderado;
    }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public int getIdApoderado() { return idApoderado; }
    public void setIdApoderado(int idApoderado) { this.idApoderado = idApoderado; }

    @Override
    public String toString() {
        return "Estudiante{idEstudiante=" + idEstudiante + ", nombres='" + nombres + " " + apellidos + "'}";
    }
}