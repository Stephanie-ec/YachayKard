USE yachaykard_db;

ALTER TABLE reporte_cierre ADD COLUMN conclusion_descriptiva TEXT NULL;

-- Comprobación: la tabla debe mostrar la columna nueva al final.
DESCRIBE reporte_cierre;
======================================================================
FIN DE 10_cierre_conclusion.sql
======================================================================

======================================================================
REEMPLAZAR TODO EL ARCHIVO: src/main/java/pe/edu/utp/yachaykard/modelo/ReporteCierre.java
(Ctrl+A, Supr y pegar desde la línea siguiente)
Qué cambia: campo conclusionDescriptiva.
======================================================================
package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Nivel final de logro de un estudiante en una competencia al cerrar el periodo.
 * El docente fija el nivel final; no es un promedio de los niveles mensuales.
 */
public class ReporteCierre implements Serializable {

    private int idReporte;
    private LocalDate fechaGeneracion;
    private String estado;
    private String nivelFinal;
    private int idEstudiante;
    private int idCompetencia;
    private int idPeriodo;
    private String conclusionDescriptiva;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
public ReporteCierre() {
    }

    /**
     * Crea un objeto ReporteCierre con todos sus datos.
     *
     * @param idReporte       identificador del reporte
     * @param fechaGeneracion fecha en que se generó
     * @param estado          Abierto o Cerrado
     * @param nivelFinal      nivel final: AD, A, B o C
     * @param idEstudiante    estudiante
     * @param idCompetencia   competencia evaluada
     * @param idPeriodo       periodo que se cierra
     */
    public ReporteCierre(int idReporte, LocalDate fechaGeneracion, String estado, String nivelFinal, int idEstudiante, int idCompetencia, int idPeriodo) {
        this.idReporte = idReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
        this.nivelFinal = nivelFinal;
        this.idEstudiante = idEstudiante;
        this.idCompetencia = idCompetencia;
        this.idPeriodo = idPeriodo;
}

    public int getIdReporte() {
        return idReporte;
}

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
}

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
}

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
}

    public String getEstado() {
        return estado;
}

    public void setEstado(String estado) {
        this.estado = estado;
}

    public String getNivelFinal() {
        return nivelFinal;
}

    public void setNivelFinal(String nivelFinal) {
        this.nivelFinal = nivelFinal;
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

    /** @return conclusión descriptiva del nivel final (obligatoria en C) */
    public String getConclusionDescriptiva() {
        return conclusionDescriptiva;
}

    public void setConclusionDescriptiva(String conclusionDescriptiva) {
        this.conclusionDescriptiva = conclusionDescriptiva;
}

    @Override
    public String toString() {
        return "ReporteCierre{" +
                "idReporte=" + idReporte +
                ", fechaGeneracion=" + fechaGeneracion +
                ", estado=" + estado +
                ", nivelFinal=" + nivelFinal +
                ", idEstudiante=" + idEstudiante +
                ", idCompetencia=" + idCompetencia +
                ", idPeriodo=" + idPeriodo +
                '}';
}
}