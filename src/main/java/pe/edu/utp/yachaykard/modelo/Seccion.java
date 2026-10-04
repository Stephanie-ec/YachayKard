package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Seccion implements Serializable {
    private int idSeccion;
    private String letra;
    private int aforo;
    private int idGrado;

    public Seccion() {}

    public Seccion(int idSeccion, String letra, int aforo, int idGrado) {
        this.idSeccion = idSeccion;
        this.letra = letra;
        this.aforo = aforo;
        this.idGrado = idGrado;
    }

    public int getIdSeccion() { return idSeccion; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }
    public String getLetra() { return letra; }
    public void setLetra(String letra) { this.letra = letra; }
    public int getAforo() { return aforo; }
    public void setAforo(int aforo) { this.aforo = aforo; }
    public int getIdGrado() { return idGrado; }
    public void setIdGrado(int idGrado) { this.idGrado = idGrado; }

    @Override
    public String toString() {
        return "Seccion{idSeccion=" + idSeccion + ", letra='" + letra + "', idGrado=" + idGrado + "}";
    }
}