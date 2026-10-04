package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

public class Periodo implements Serializable {
    private int idPeriodo;
    private String nombrePeriodo;

    public Periodo() {}

    public Periodo(int idPeriodo, String nombrePeriodo) {
        this.idPeriodo = idPeriodo;
        this.nombrePeriodo = nombrePeriodo;
    }

    public int getIdPeriodo() { return idPeriodo; }
    public void setIdPeriodo(int idPeriodo) { this.idPeriodo = idPeriodo; }
    public String getNombrePeriodo() { return nombrePeriodo; }
    public void setNombrePeriodo(String nombrePeriodo) { this.nombrePeriodo = nombrePeriodo; }

    @Override
    public String toString() {
        return "Periodo{idPeriodo=" + idPeriodo + ", nombrePeriodo='" + nombrePeriodo + "'}";
    }
}