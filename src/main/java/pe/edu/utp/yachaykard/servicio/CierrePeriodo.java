package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.dao.impl.ReporteCierreDAOImpl;
import pe.edu.utp.yachaykard.modelo.ReporteCierre;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Cierre del periodo (RF-30): el docente decide el nivel final de cada competencia por
 * predominancia de los niveles mensuales. El sistema SUGIERE el nivel más frecuente
 * (en empate, el del último mes) y el docente lo confirma o lo cambia.
 * La conclusión descriptiva es obligatoria en C y, si se escribe, tiene al menos 10 caracteres,
 * como pide la plantilla RegNotas del SIAGIE.
 * El DAO se recibe en el constructor para poder simularlo con Mockito en las pruebas.
 */
public class CierrePeriodo {

    public static final String CERRADO = "Cerrado";
    static final Set<String> NIVELES = Set.of("AD", "A", "B", "C");
    static final int LARGO_MINIMO_CONCLUSION = 10;

    private final ReporteCierreDAO reporteDAO;

    /**
     * Constructor que usa la aplicación: trabaja con la base de datos.
     */
    public CierrePeriodo() {
        this(new ReporteCierreDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param reporteDAO acceso al reporte de cierre
     */
    public CierrePeriodo(ReporteCierreDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

    /**
     * Nivel sugerido por predominancia.
     *
     * @param nivelesPorMes niveles mensuales en orden de mes (puede tener null si falta un mes)
     * @return el nivel más frecuente; en empate, el del mes más reciente entre los empatados;
     * null si no hay niveles
     */
    public String sugerirNivel(List<String> nivelesPorMes) {
        Map<String, Integer> frecuencia = new HashMap<>();
        for (String nivel : nivelesPorMes) {
            if (nivel != null) {
                frecuencia.merge(nivel, 1, Integer::sum);
            }
        }
        if (frecuencia.isEmpty()) {
            return null;
        }
        int maximo = frecuencia.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        for (int i = nivelesPorMes.size() - 1; i >= 0; i--) {
            String nivel = nivelesPorMes.get(i);
            if (nivel != null && frecuencia.get(nivel) == maximo) {
                return nivel;
            }
        }
        return null;
    }

    /**
     * Revisa el nivel final y la conclusión de un estudiante.
     *
     * @param nivel      nivel final elegido
     * @param conclusion conclusión descriptiva
     * @return mensaje de error, o null si todo es correcto
     */
    public String validar(String nivel, String conclusion) {
        if (nivel == null || !NIVELES.contains(nivel)) {
            return "Elija el nivel final (AD, A, B o C).";
        }
        String texto = conclusion == null ? "" : conclusion.trim();
        if ("C".equals(nivel) && texto.isEmpty()) {
            return "En el nivel C la conclusión descriptiva es obligatoria.";
        }
        if (!texto.isEmpty() && texto.length() < LARGO_MINIMO_CONCLUSION) {
            return "La conclusión descriptiva debe tener al menos " + LARGO_MINIMO_CONCLUSION + " caracteres.";
        }
        return null;
    }

    /**
     * Guarda y cierra los niveles finales. Si algún estudiante tiene un error, no guarda ninguno.
     *
     * @param reportes niveles finales (uno por estudiante) de una competencia y un periodo
     * @param hoy      fecha del cierre
     * @return lista de errores (con el estudiante indicado por su posición); vacía si se guardó todo
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<String> cerrar(List<ReporteCierre> reportes, LocalDate hoy) throws SQLException {
        List<String> errores = new ArrayList<>();
        for (int i = 0; i < reportes.size(); i++) {
            ReporteCierre r = reportes.get(i);
            String error = validar(r.getNivelFinal(), r.getConclusionDescriptiva());
            if (error != null) {
                errores.add("Estudiante N.° " + (i + 1) + ": " + error);
            }
        }
        if (!errores.isEmpty()) {
            return errores;
        }
        for (ReporteCierre r : reportes) {
            String texto = r.getConclusionDescriptiva() == null ? "" : r.getConclusionDescriptiva().trim();
            r.setConclusionDescriptiva(texto.isEmpty() ? null : texto);
            r.setEstado(CERRADO);
            r.setFechaGeneracion(hoy);
            ReporteCierre existente = reporteDAO.buscar(r.getIdEstudiante(), r.getIdCompetencia(), r.getIdPeriodo());
            if (existente == null) {
                reporteDAO.insertar(r);
            } else if (!CERRADO.equals(existente.getEstado())
                    || !Objects.equals(existente.getNivelFinal(), r.getNivelFinal())
                    || !Objects.equals(existente.getConclusionDescriptiva(), r.getConclusionDescriptiva())) {
                r.setIdReporte(existente.getIdReporte());
                reporteDAO.actualizar(r);
            }
        }
        return errores;
    }
}