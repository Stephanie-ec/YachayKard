package pe.edu.utp.yachaykard.modelo;


import java.io.Serializable;

/*Área o curso del plan de estudios, con su código de SIAGIE.*/
public class Curso implements Serializable {

    private int idCurso;
    private String nombreCurso;
    private int horasSemanales;
    private String codigoSiagie;

    /*Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Curso() {
    }

    /**
     * Crea un objeto Curso con todos sus datos.
     *
     * @param idCurso        identificador del curso
     * @param nombreCurso    nombre del curso o área
     * @param horasSemanales horas semanales
     * @param codigoSiagie   código del área en SIAGIE
     */
    public Curso(int idCurso, String nombreCurso, int horasSemanales, String codigoSiagie) {
        this.idCurso = idCurso;
        this.nombreCurso = nombreCurso;
        this.horasSemanales = horasSemanales;
        this.codigoSiagie = codigoSiagie;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public int getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(int horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public String getCodigoSiagie() {
        return codigoSiagie;
    }

    public void setCodigoSiagie(String codigoSiagie) {
        this.codigoSiagie = codigoSiagie;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "idCurso=" + idCurso +
                ", nombreCurso=" + nombreCurso +
                ", horasSemanales=" + horasSemanales +
                ", codigoSiagie=" + codigoSiagie +
                '}';
    }

}