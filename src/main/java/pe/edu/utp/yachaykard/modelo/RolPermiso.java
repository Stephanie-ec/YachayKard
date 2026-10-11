package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Relación N:M "otorga" entre Rol y Permiso: indica qué permisos tiene cada rol.
 * Su clave primaria es compuesta (idRol, idPermiso).
 */
public class RolPermiso implements Serializable {

    private int idRol;
    private int idPermiso;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public RolPermiso() {
    }

    /**
     * Crea la relación entre un rol y un permiso.
     *
     * @param idRol     rol que recibe el permiso
     * @param idPermiso permiso otorgado
     */
    public RolPermiso(int idRol, int idPermiso) {
        this.idRol = idRol;
        this.idPermiso = idPermiso;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(int idPermiso) {
        this.idPermiso = idPermiso;
    }

    @Override
    public String toString() {
        return "RolPermiso{" +
                "idRol=" + idRol +
                ", idPermiso=" + idPermiso +
                '}';
    }
}
