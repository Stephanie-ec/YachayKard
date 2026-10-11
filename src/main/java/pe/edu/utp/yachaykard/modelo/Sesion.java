package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Registro de una sesión de usuario en el sistema.
 * La sesión queda abierta mientras fechaFin sea null.
 */
public class Sesion implements Serializable {

    private int idSesion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int idUsuario;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Sesion() {
    }

    /**
     * Crea una sesión con todos sus datos.
     *
     * @param idSesion    identificador de la sesión
     * @param fechaInicio fecha y hora en que el usuario inició sesión
     * @param fechaFin    fecha y hora de cierre; null si la sesión sigue abierta
     * @param idUsuario   usuario que inició la sesión
     */
    public Sesion(int idSesion, LocalDateTime fechaInicio, LocalDateTime fechaFin, int idUsuario) {
        this.idSesion = idSesion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
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
                ", fechaFin=" + fechaFin +
                ", idUsuario=" + idUsuario +
                '}';
    }
}