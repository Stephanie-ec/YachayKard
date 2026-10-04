package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Docente implements Serializable {
    private int idDocente;
    private String dni;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private Integer idUsuario;

    public Docente() {}

    public Docente(int idDocente, String dni, String nombres, String apellidos,
                   String especialidad, Integer idUsuario) {
        this.idDocente = idDocente;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.idUsuario = idUsuario;
    }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    @Override
    public String toString() {
        return "Docente{idDocente=" + idDocente + ", nombres='" + nombres + " " + apellidos + "'}";
    }
}