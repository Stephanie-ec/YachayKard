package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Permiso que habilita una acción o pantalla del sistema (por ejemplo, "Registrar calificaciones").
 * Se asigna a los roles mediante la tabla rol_permiso.
 */
public class Permiso implements Serializable {

    private int idPermiso;
    private String nombrePermiso;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Permiso() {
    }

    /**
     * Crea un permiso con todos sus datos.
     *
     * @param idPermiso     identificador del permiso
     * @param nombrePermiso nombre descriptivo del permiso
     */
    public Permiso(int idPermiso, String nombrePermiso) {
        this.idPermiso = idPermiso;
        this.nombrePermiso = nombrePermiso;
    }

    public int getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(int idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getNombrePermiso() {
        return nombrePermiso;
    }

    public void setNombrePermiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "idPermiso=" + idPermiso +
                ", nombrePermiso='" + nombrePermiso + '\'' +
                '}';
    }
}