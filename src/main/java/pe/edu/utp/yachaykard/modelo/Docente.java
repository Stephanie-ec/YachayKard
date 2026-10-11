package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/*Docente de aula. Inicia sesión con su usuario; si tiene una sección a cargo, también es tutor.*/

public class Docente implements Serializable {

    private int idDocente;
    private String dni;
    private String nombres;
    private String apellidos;
    private String especialidad;
    private int idUsuario;

    /*Constructor vacío requerido por JSF y por el DAO al mapear filas.*/

    public Docente() {
    }

    /**
     * Crea un objeto Docente con todos sus datos.
     *
     * @param idDocente    identificador del docente
     * @param dni          DNI de 8 dígitos
     * @param nombres      nombres
     * @param apellidos    apellidos
     * @param especialidad especialidad o área
     * @param idUsuario    usuario con el que inicia sesión
     */
    public Docente(int idDocente, String dni, String nombres, String apellidos, String especialidad, int idUsuario) {
        this.idDocente = idDocente;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.idUsuario = idUsuario;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Docente{" + "idDocente=" + idDocente + ", dni='" + dni + '\'' + ", nombres='" + nombres + '\'' + ", apellidos='" + apellidos + '\'' + ", especialidad='" + especialidad + '\'' + ", idUsuario=" + idUsuario + '}';
    }
}