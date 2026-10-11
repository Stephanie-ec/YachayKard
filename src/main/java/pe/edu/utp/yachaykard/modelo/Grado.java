package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

//Grado de estudios (por ejemplo, 1.° de secundaria).*/
public class Grado implements Serializable {

    private int idGrado;
    private String numeroGrado;
    private String nivel;

    /*
    Constructor vacío
    requerido por
    JSF y
    por el
    DAO al
    mapear filas.*/

    public Grado() {
    }

    /**
     * Crea un objeto Grado con todos sus datos.
     *
     * @param idGrado     identificador del grado
     * @param numeroGrado número o nombre del grado
     * @param nivel       nivel educativo (Primaria o Secundaria)
     */
    public Grado(int idGrado, String numeroGrado, String nivel) {
        this.idGrado = idGrado;
        this.numeroGrado = numeroGrado;
        this.nivel = nivel;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getNumeroGrado() {
        return numeroGrado;
    }

    public void setNumeroGrado(String numeroGrado) {
        this.numeroGrado = numeroGrado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return "Grado{" +
                "idGrado=" + idGrado +
                ", numeroGrado='" + numeroGrado + '\'' +
                ", nivel='" + nivel + '\'' +
                '}';
    }
}