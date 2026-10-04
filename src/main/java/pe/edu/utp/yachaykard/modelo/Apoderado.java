package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Apoderado implements Serializable {
    private int idApoderado;
    private String dni;
    private String nombres;
    private String telefono;

    public Apoderado() {}

    public Apoderado(int idApoderado, String dni, String nombres, String telefono) {
        this.idApoderado = idApoderado;
        this.dni = dni;
        this.nombres = nombres;
        this.telefono = telefono;
    }

    public int getIdApoderado() { return idApoderado; }
    public void setIdApoderado(int idApoderado) { this.idApoderado = idApoderado; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return "Apoderado{idApoderado=" + idApoderado + ", nombres='" + nombres + "'}";
    }
}