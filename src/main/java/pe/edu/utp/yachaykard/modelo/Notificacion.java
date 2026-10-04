package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notificacion implements Serializable {
    private int idNotificacion;
    private LocalDateTime fechaEnvio;
    private String mensaje;
    private int idApoderado;

    public Notificacion() {}

    public Notificacion(int idNotificacion, LocalDateTime fechaEnvio, String mensaje, int idApoderado) {
        this.idNotificacion = idNotificacion;
        this.fechaEnvio = fechaEnvio;
        this.mensaje = mensaje;
        this.idApoderado = idApoderado;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public int getIdApoderado() { return idApoderado; }
    public void setIdApoderado(int idApoderado) { this.idApoderado = idApoderado; }

    @Override
    public String toString() {
        return "Notificacion{idNotificacion=" + idNotificacion + ", fechaEnvio=" + fechaEnvio + "}";
    }
}