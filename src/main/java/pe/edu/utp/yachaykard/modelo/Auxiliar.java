package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Auxiliar de educación. Registra la asistencia diaria, las justificaciones de un día
 * y las incidencias fuera del aula. Inicia sesión con su propio usuario.
 */
public class Auxiliar implements Serializable {

    private int idAuxiliar;
    private String dni;
    private String nombres;
    private String apellidos;
    private int idUsuario;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Auxiliar() {
    }

    /**
     * Crea un auxiliar con todos sus datos.
     *
     * @param idAuxiliar identificador del auxiliar
     * @param dni        DNI de 8 dígitos
     * @param nombres    nombres
     * @param apellidos  apellidos
     * @param idUsuario  usuario con el que inicia sesión
     */
    public Auxiliar(int idAuxiliar, String dni, String nombres, String apellidos, int idUsuario) {
        this.idAuxiliar = idAuxiliar;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.idUsuario = idUsuario;
    }

    public int getIdAuxiliar() {
        return idAuxiliar;
    }

    public void setIdAuxiliar(int idAuxiliar) {
        this.idAuxiliar = idAuxiliar;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Auxiliar{" +
                "idAuxiliar=" + idAuxiliar +
                ", dni='" + dni + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", idUsuario=" + idUsuario +
                '}';
    }
}