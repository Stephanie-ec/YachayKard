package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Rol de un usuario del sistema: Administrador, Docente o Auxiliar.
 * El rol define qué permisos tiene el usuario (relación "otorga" con Permiso).
 */
public class Rol implements Serializable {

    private int idRol;
    private String nombreRol;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Rol() {
    }

    /**
     * Crea un rol con todos sus datos.
     *
     * @param idRol     identificador del rol
     * @param nombreRol nombre del rol (Administrador, Docente o Auxiliar)
     */
    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "idRol=" + idRol +
                ", nombreRol='" + nombreRol + '\'' +
                '}';
    }
}