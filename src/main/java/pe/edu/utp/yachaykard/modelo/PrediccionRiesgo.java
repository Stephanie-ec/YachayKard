package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Resultado de la predicción del nivel de riesgo de un estudiante (Bajo, Medio o Alto),
 * calculado a partir de su asistencia, conducta y calificaciones.
 * Se guarda un registro por cálculo para conservar el historial.
 */
public class PrediccionRiesgo implements Serializable {

    private int idPrediccion;
    private int idEstudiante;
    private String nivelRiesgo;
    private String motivo;
    private LocalDate fechaCalculo;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public PrediccionRiesgo() {
    }

    /**
     * Crea una predicción con todos sus datos.
     *
     * @param idPrediccion identificador de la predicción
     * @param idEstudiante estudiante evaluado
     * @param nivelRiesgo  Bajo, Medio o Alto
     * @param motivo       explicación breve de por qué se asignó ese nivel
     * @param fechaCalculo fecha en que se calculó
     */
    public PrediccionRiesgo(int idPrediccion, int idEstudiante, String nivelRiesgo,
                            String motivo, LocalDate fechaCalculo) {
        this.idPrediccion = idPrediccion;
        this.idEstudiante = idEstudiante;
        this.nivelRiesgo = nivelRiesgo;
        this.motivo = motivo;
        this.fechaCalculo = fechaCalculo;
    }

    public int getIdPrediccion() {
        return idPrediccion;
    }

    public void setIdPrediccion(int idPrediccion) {
        this.idPrediccion = idPrediccion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDate getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDate fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    @Override
    public String toString() {
        return "PrediccionRiesgo{" +
                "idPrediccion=" + idPrediccion +
                ", idEstudiante=" + idEstudiante +
                ", nivelRiesgo='" + nivelRiesgo + '\'' +
                ", motivo='" + motivo + '\'' +
                ", fechaCalculo=" + fechaCalculo +
                '}';
    }
}