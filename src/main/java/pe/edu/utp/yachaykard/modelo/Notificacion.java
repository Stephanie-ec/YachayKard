package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Aviso generado por el sistema: interno (bandeja del tutor o del administrador)
 * o mensaje de WhatsApp preparado para el apoderado.
 */
public class Notificacion implements Serializable {

    private int idNotificacion;
    private LocalDateTime fechaEnvio;
    private String mensaje;
    private Integer idApoderado;
    private Integer idUsuario;
    private int idEstudiante;
    private String canal;
    private String tipo;
    private String estado;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Notificacion() {
    }

    /**
     * Crea un objeto Notificacion con todos sus datos.
     *
     * @param idNotificacion identificador de la notificación
     * @param fechaEnvio     fecha y hora de envío
     * @param mensaje        texto del aviso
     * @param idApoderado    apoderado destinatario; null si es interna
     * @param idUsuario      usuario destinatario; null si es para el apoderado
     * @param idEstudiante   estudiante al que se refiere
     * @param canal          Interno o WhatsApp
     * @param tipo           tipo de aviso (Incidencia, Faltas, Riesgo, Resumen mensual)
     * @param estado         Pendiente o Enviado
     */
    public Notificacion(int idNotificacion, LocalDateTime fechaEnvio, String mensaje, Integer idApoderado, Integer idUsuario, int idEstudiante, String canal, String tipo, String estado) {
        this.idNotificacion = idNotificacion;
        this.fechaEnvio = fechaEnvio;
        this.mensaje = mensaje;
        this.idApoderado = idApoderado;
        this.idUsuario = idUsuario;
        this.idEstudiante = idEstudiante;
        this.canal = canal;
        this.tipo = tipo;
        this.estado = estado;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdApoderado() {
        return idApoderado;
    }

    public void setIdApoderado(Integer idApoderado) {
        this.idApoderado = idApoderado;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", fechaEnvio=" + fechaEnvio +
                ", mensaje=" + mensaje +
                ", idApoderado=" + idApoderado +
                ", idUsuario=" + idUsuario +
                ", idEstudiante=" + idEstudiante +
                ", canal=" + canal +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}