package pe.edu.utp.yachaykard.servicio;

/**
 * Resumen mensual de asistencia de un estudiante (una fila del consolidado, RF-21).
 */
public class ResumenAsistencia {

    private final int idEstudiante;
    private int presentes;
    private int tardanzas;
    private int faltas;
    private int faltasJustificadas;

    /**
     * @param idEstudiante estudiante al que pertenece el resumen
     */
    public ResumenAsistencia(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    void sumarPresente() {
        presentes++;
    }

    void sumarTardanza() {
        tardanzas++;
    }

    void sumarFalta(boolean justificada) {
        if (justificada) {
            faltasJustificadas++;
        } else {
            faltas++;
        }
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getPresentes() {
        return presentes;
    }

    public int getTardanzas() {
        return tardanzas;
    }

    /** @return faltas sin justificar */
    public int getFaltas() {
        return faltas;
    }

    public int getFaltasJustificadas() {
        return faltasJustificadas;
    }

    /** @return días con registro de asistencia en el mes */
    public int getDiasRegistrados() {
        return presentes + tardanzas + faltas + faltasJustificadas;
    }

    /**
     * Porcentaje de asistencia: presentes y tardanzas sobre los días registrados.
     *
     * @return valor de 0 a 100 con un decimal; 0 si no hay registros
     */
    public double getPorcentajeAsistencia() {
        int dias = getDiasRegistrados();
        if (dias == 0) {
            return 0;
        }
        return Math.round((presentes + tardanzas) * 1000.0 / dias) / 10.0;
    }
}
