package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.impl.AsistenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.Asistencia;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Consolidado mensual de asistencia por sección (RF-21): cuenta, para cada estudiante,
 * los días presentes, tardanzas, faltas sin justificar y faltas justificadas del mes.
 * El DAO se recibe en el constructor para poder simularlo con Mockito en las pruebas.
 */
public class ConsolidadoAsistencia {

    public static final String PRESENTE = "Presente";
    public static final String TARDANZA = "Tardanza";
    public static final String FALTA = "Falta";

    private final AsistenciaDAO asistenciaDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public ConsolidadoAsistencia() {
        this(new AsistenciaDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param asistenciaDAO acceso a la asistencia
     */
    public ConsolidadoAsistencia(AsistenciaDAO asistenciaDAO) {
        this.asistenciaDAO = asistenciaDAO;
    }

    /**
     * Arma el consolidado de una sección en un mes.
     *
     * @param idSeccion sección
     * @param anio      año
     * @param mes       mes de 1 a 12
     * @return una fila por estudiante, en el orden en que llegan de la base de datos
     * @throws IllegalArgumentException si el mes no está entre 1 y 12
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public List<ResumenAsistencia> generar(int idSeccion, int anio, int mes) throws SQLException {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12.");
        }
        return consolidar(asistenciaDAO.listarPorSeccionYMes(idSeccion, anio, mes));
    }

    /**
     * Agrupa los registros por estudiante y cuenta cada estado.
     * Una falta con justificación cuenta como falta justificada.
     *
     * @param registros asistencia del mes
     * @return una fila por estudiante
     */
    public List<ResumenAsistencia> consolidar(List<Asistencia> registros) {
        Map<Integer, ResumenAsistencia> porEstudiante = new LinkedHashMap<>();
        for (Asistencia a : registros) {
            ResumenAsistencia resumen = porEstudiante.computeIfAbsent(a.getIdEstudiante(), ResumenAsistencia::new);
            if (PRESENTE.equals(a.getEstado())) {
                resumen.sumarPresente();
            } else if (TARDANZA.equals(a.getEstado())) {
                resumen.sumarTardanza();
            } else if (FALTA.equals(a.getEstado())) {
                resumen.sumarFalta(a.getIdJustificacion() != null);
            }
        }
        return new ArrayList<>(porEstudiante.values());
    }
}
