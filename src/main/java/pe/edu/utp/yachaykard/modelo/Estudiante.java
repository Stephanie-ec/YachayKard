package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/* Estudiante de la institución. Es el sujeto de los datos: no inicia sesión .*/

public class Estudiante implements Serializable {

    private int idEstudiante;
    private String dni;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private int idApoderado;
    private String idSiagie;
    private String codigoEstudiante;

    /*Constructor vacío
    requerido por
    JSF y
    por el
    DAO al
    mapear filas.*/

    public Estudiante() {
    }

    /**
     * Crea un objeto Estudiante con todos sus datos.
     *
     * @param idEstudiante     identificador del estudiante
     * @param dni              DNI de 8 dígitos
     * @param nombres          nombres
     * @param apellidos        apellidos
     * @param fechaNacimiento  fecha de nacimiento
     * @param idApoderado      apoderado responsable
     * @param idSiagie         ID del estudiante en SIAGIE
     * @param codigoEstudiante código de estudiante de 14 dígitos
     */
    public Estudiante(int idEstudiante, String dni, String nombres, String apellidos, LocalDate fechaNacimiento, int idApoderado, String idSiagie, String codigoEstudiante) {
        this.idEstudiante = idEstudiante;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.idApoderado = idApoderado;
        this.idSiagie = idSiagie;
        this.codigoEstudiante = codigoEstudiante;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getIdApoderado() {
        return idApoderado;
    }

    public void setIdApoderado(int idApoderado) {
        this.idApoderado = idApoderado;
    }

    public String getIdSiagie() {
        return idSiagie;
    }

    public void setIdSiagie(String idSiagie) {
        this.idSiagie = idSiagie;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante=" + idEstudiante +
                ", dni=" + dni +
                ", nombres=" + nombres +
                ", apellidos=" + apellidos +
                ", fechaNacimiento=" + fechaNacimiento +
                ", idApoderado=" + idApoderado +
                ", idSiagie=" + idSiagie +
                ", codigoEstudiante=" + codigoEstudiante +
                '}';
    }
}