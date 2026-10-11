package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Sección de un grado (por ejemplo, 3.° "B"), con su docente tutor y su auxiliar.
 */
public class Seccion implements Serializable {

    private int idSeccion;
    private char letra;
    private int aforo;
    private int idGrado;
    private Integer idDocenteTutor;
    private Integer idAuxiliar;

    /**
     * Constructor vacío requerido por JSF y por el DAO al mapear filas.
     */
    public Seccion() {
    }

    /**
     * Crea un objeto Seccion con todos sus datos.
     *
     * @param idSeccion      identificador de la sección
     * @param letra          letra de la sección
     * @param aforo          capacidad máxima de estudiantes
     * @param idGrado        grado al que pertenece
     * @param idDocenteTutor docente tutor; null si aún no se asigna
     * @param idAuxiliar     auxiliar que atiende la sección; null si aún no se asigna
     */
    public Seccion(int idSeccion, char letra, int aforo, int idGrado, Integer idDocenteTutor, Integer idAuxiliar) {
        this.idSeccion = idSeccion;
        this.letra = letra;
        this.aforo = aforo;
        this.idGrado = idGrado;
        this.idDocenteTutor = idDocenteTutor;
        this.idAuxiliar = idAuxiliar;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public char getLetra() {
        return letra;
    }

    public void setLetra(char letra) {
        this.letra = letra;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public Integer getIdDocenteTutor() {
        return idDocenteTutor;
    }

    public void setIdDocenteTutor(Integer idDocenteTutor) {
        this.idDocenteTutor = idDocenteTutor;
    }

    public Integer getIdAuxiliar() {
        return idAuxiliar;
    }

    public void setIdAuxiliar(Integer idAuxiliar) {
        this.idAuxiliar = idAuxiliar;
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "idSeccion=" + idSeccion +
                ", letra=" + letra +
                ", aforo=" + aforo +
                ", idGrado=" + idGrado +
                ", idDocenteTutor=" + idDocenteTutor +
                ", idAuxiliar=" + idAuxiliar +
                '}';
    }
}