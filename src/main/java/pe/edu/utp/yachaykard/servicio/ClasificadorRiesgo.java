package pe.edu.utp.yachaykard.servicio;

/**
 * Clasifica el riesgo de deserción de un estudiante a partir de sus tres señales de alerta.
 * La aplicación usa el modelo de Weka ({@link ModeloRiesgoWeka}); las pruebas pueden usar
 * una clasificación simulada.
 */
@FunctionalInterface
public interface ClasificadorRiesgo {

    /**
     * @param faltas            faltas sin justificar en los últimos 30 días
     * @param competenciasEnC   competencias cuya calificación más reciente es C
     * @param incidenciasGraves incidencias graves en los últimos 60 días
     * @return Alto, Medio o Bajo
     */
    String clasificar(int faltas, int competenciasEnC, int incidenciasGraves);
}