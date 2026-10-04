package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Sesion implements Serializable {

    private int idSesion;
    private LocalDateTime fechaInicio;
    private int idUsuario;

    public Sesion() {
    }

    public Sesion(int idSesion, LocalDateTime fechaInicio, int idUsuario) {
        this.idSesion = idSesion;
        this.fechaInicio = fechaInicio;
        this.idUsuario = idUsuario;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Sesion{" +
                "idSesion=" + idSesion +
                ", fechaInicio=" + fechaInicio +
                ", idUsuario=" + idUsuario +
                '}';
    }
}