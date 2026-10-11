
package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/*Padre, madre o apoderado del estudiante. No inicia sesión;
recibe los avisos por WhatsApp en el teléfono de la ficha de matrícula.*/
public class Apoderado implements Serializable {

    private int idApoderado;
    private String dni;
    private String nombres;
    private String telefono;

    /*
    Constructor vacío
    requerido por
    JSF y
    por el
    DAO al
    mapear filas.*/

    public Apoderado() {
    }

    /**
     * Crea un objeto Apoderado con todos sus datos.
     *
     * @param idApoderado identificador del apoderado
     * @param dni         DNI de 8 dígitos
     * @param nombres     nombres completos
     * @param telefono    teléfono para los avisos por WhatsApp
     */
    public Apoderado(int idApoderado, String dni, String nombres, String telefono) {
        this.idApoderado = idApoderado;
        this.dni = dni;
        this.nombres = nombres;
        this.telefono = telefono;
    }

    public int getIdApoderado() {
        return idApoderado;
    }

    public void setIdApoderado(int idApoderado) {
        this.idApoderado = idApoderado;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Apoderado{" +
                "idApoderado=" + idApoderado +
                ", dni=" + dni +
                ", nombres=" + nombres +
                ", telefono=" + telefono +
                '}';
    }

}