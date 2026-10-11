package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Nivel de logro mensual (AD, A, B o C) de un estudiante en una competencia.
 * El nivel final del periodo no va aquí: se registra en ReporteCierre.
 */
public class Calificacion implements Serializable {

    private int idCalificacion;
    private int idEstudiante;
    private int idCompetencia;
    private int idPeriodo;
    private String observacion;
    private int mes;
    private String nivelMensual;
    private String fortaleza;
    private String debilidad;
    private String conclusionDescriptiva;

    /**
     * Constructor vacío requerido por JSF y por el DAO al mapear filas.
     */
    public Calificacion() {
    }

    /**
     * Crea un objeto Calificacion con todos sus datos.
     *
     * @param idCalificacion        identificador de la calificación
     * @param idEstudiante          estudiante evaluado
     * @param idCompetencia         competencia evaluada
     * @param idPeriodo             periodo al que pertenece
     * @param observacion           observación corta opcional
     * @param mes                   mes de la evaluación (1 a 12)
     * @param nivelMensual          nivel de logro: AD, A, B o C
     * @param fortaleza             fortaleza observada
     * @param debilidad             debilidad observada
     * @param conclusionDescriptiva conclusión descriptiva (obligatoria para B y C)
     */
    public Calificacion(int idCalificacion, int idEstudiante, int idCompetencia, int idPeriodo, String observacion, int mes, String nivelMensual, String fortaleza, String debilidad, String conclusionDescriptiva) {
        this.idCalificacion = idCalificacion;
        this.idEstudiante = idEstudiante;
        this.idCompetencia = idCompetencia;
        this.idPeriodo = idPeriodo;
        this.observacion = observacion;
        this.mes = mes;
        this.nivelMensual = nivelMensual;
        this.fortaleza = fortaleza;
        this.debilidad = debilidad;
        this.conclusionDescriptiva = conclusionDescriptiva;
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdCompetencia() {
        return idCompetencia;
    }

    public void setIdCompetencia(int idCompetencia) {
        this.idCompetencia = idCompetencia;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getNivelMensual() {
        return nivelMensual;
    }

    public void setNivelMensual(String nivelMensual) {
        this.nivelMensual = nivelMensual;
    }

    public String getFortaleza() {
        return fortaleza;
    }

    public void setFortaleza(String fortaleza) {
        this.fortaleza = fortaleza;
    }

    public String getDebilidad() {
        return debilidad;
    }

    public void setDebilidad(String debilidad) {
        this.debilidad = debilidad;
    }

    public String getConclusionDescriptiva() {
        return conclusionDescriptiva;
    }

    public void setConclusionDescriptiva(String conclusionDescriptiva) {
        this.conclusionDescriptiva = conclusionDescriptiva;
    }

    @Override
    public String toString() {
        return "Calificacion{" +
                "idCalificacion=" + idCalificacion +
                ", idEstudiante=" + idEstudiante +
                ", idCompetencia=" + idCompetencia +
                ", idPeriodo=" + idPeriodo +
                ", observacion=" + observacion +
                ", mes=" + mes +
                ", nivelMensual=" + nivelMensual +
                ", fortaleza=" + fortaleza +
                ", debilidad=" + debilidad +
                ", conclusionDescriptiva=" + conclusionDescriptiva +
                '}';
    }
}