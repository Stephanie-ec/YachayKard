package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Cuenta de acceso al sistema. Solo Administrador, Docente y Auxiliar tienen usuario;
 * el apoderado y el estudiante no inician sesión.
 */
public class Usuario implements Serializable {

    private int idUsuario;
    private String username;
    private String password;
    private int idRol;
    private int idInstitucion;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Usuario() {
    }

    /**
     * Crea un usuario con todos sus datos.
     *
     * @param idUsuario     identificador del usuario
     * @param username      nombre de usuario (único)
     * @param password      contraseña
     * @param idRol         rol del usuario
     * @param idInstitucion institución a la que pertenece
     */
    public Usuario(int idUsuario, String username, String password, int idRol, int idInstitucion) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.idRol = idRol;
        this.idInstitucion = idInstitucion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", idRol=" + idRol +
                ", idInstitucion=" + idInstitucion +
                '}';
    }
}