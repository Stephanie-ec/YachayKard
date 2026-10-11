package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/*
Periodo académico configurable (bimestre o trimestre) que el administrador abre o cierra.*/
public class Periodo implements Serializable {

    private int idPeriodo;
    private String nombrePeriodo;
    private String tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    /* Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Periodo() {
    }

    /**
     * Crea un objeto Periodo con todos sus datos.
     *
     * @param idPeriodo     identificador del periodo
     * @param nombrePeriodo nombre (por ejemplo, I Trimestre)
     * @param tipo          Bimestre o Trimestre
     * @param fechaInicio   fecha de inicio
     * @param fechaFin      fecha de fin
     * @param estado        Abierto o Cerrado
     */
    public Periodo(int idPeriodo, String nombrePeriodo, String tipo, LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.idPeriodo = idPeriodo;
        this.nombrePeriodo = nombrePeriodo;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Periodo{" +
                "idPeriodo=" + idPeriodo +
                ", nombrePeriodo=" + nombrePeriodo +
                ", tipo=" + tipo +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado=" + estado +
                '}';
    }
}