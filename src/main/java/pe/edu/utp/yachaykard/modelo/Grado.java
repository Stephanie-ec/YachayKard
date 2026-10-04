package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Grado implements Serializable {
    private int idGrado;
    private String numeroGrado;
    private String nivel;

    public Grado() {}

    public Grado(int idGrado, String numeroGrado, String nivel) {
        this.idGrado = idGrado;
        this.numeroGrado = numeroGrado;
        this.nivel = nivel;
    }

    public int getIdGrado() { return idGrado; }
    public void setIdGrado(int idGrado) { this.idGrado = idGrado; }
    public String getNumeroGrado() { return numeroGrado; }
    public void setNumeroGrado(String numeroGrado) { this.numeroGrado = numeroGrado; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    @Override
    public String toString() {
        return "Grado{idGrado=" + idGrado + ", numeroGrado='" + numeroGrado + "', nivel='" + nivel + "'}";
    }
}