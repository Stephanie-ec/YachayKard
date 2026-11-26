package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.CalificacionDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.dao.PrediccionRiesgoDAO;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Calificacion;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del cálculo de riesgo (modelo y reglas de respaldo), con los DAO simulados en Mockito. Datos ficticios.
 */
class PrediccionRiesgoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 16);

    private EstudianteDAO estudianteDAO;
    private AsistenciaDAO asistenciaDAO;
    private CalificacionDAO calificacionDAO;
    private IncidenciaDAO incidenciaDAO;
    private PrediccionRiesgoDAO prediccionDAO;
    private PrediccionRiesgoService servicio;

    @BeforeEach
    void preparar() throws SQLException {
        estudianteDAO = mock(EstudianteDAO.class);
        asistenciaDAO = mock(AsistenciaDAO.class);
        calificacionDAO = mock(CalificacionDAO.class);
        incidenciaDAO = mock(IncidenciaDAO.class);
        prediccionDAO = mock(PrediccionRiesgoDAO.class);
        servicio = new PrediccionRiesgoService(estudianteDAO, asistenciaDAO, calificacionDAO, incidenciaDAO, prediccionDAO);
        when(asistenciaDAO.listarPorEstudiante(anyInt())).thenReturn(List.of());
        when(calificacionDAO.listarPorEstudiante(anyInt())).thenReturn(List.of());
        when(incidenciaDAO.listarPorEstudiante(anyInt())).thenReturn(List.of());
    }

    private static Asistencia falta(LocalDate fecha, Integer idJustificacion) {
        Asistencia a = new Asistencia();
        a.setFecha(fecha);
        a.setEstado("Falta");
        a.setIdJustificacion(idJustificacion);
        return a;
    }

    private static Calificacion nota(int idCompetencia, int idPeriodo, int mes, String nivel) {
        Calificacion c = new Calificacion();
        c.setIdCompetencia(idCompetencia);
        c.setIdPeriodo(idPeriodo);
        c.setMes(mes);
        c.setNivelMensual(nivel);
        return c;
    }

    private static Incidencia incidencia(String gravedad, LocalDate fecha) {
        Incidencia i = new Incidencia();
        i.setGravedad(gravedad);
        i.setFechaHora(fecha.atTime(10, 0));
        return i;
    }

    @Test
    @DisplayName("Sin señales el riesgo es bajo; con una, medio; con dos o más, alto")
    void nivelSegunSenales() {
        assertEquals("Bajo", servicio.nivel(2, 0, 0));
        assertEquals("Medio", servicio.nivel(3, 0, 0));
        assertEquals("Medio", servicio.nivel(0, 1, 0));
        assertEquals("Alto", servicio.nivel(3, 1, 0));
        assertEquals("Alto", servicio.nivel(4, 2, 1));
    }

    @Test
    @DisplayName("Solo cuentan las faltas sin justificar de los últimos 30 días")
    void cuentaFaltasRecientesSinJustificar() {
        List<Asistencia> registros = List.of(
                falta(HOY, null),
                falta(HOY.minusDays(29), null),
                falta(HOY.minusDays(30), null),
                falta(HOY.minusDays(5), 7));

        assertEquals(2, servicio.faltasSinJustificar(registros, HOY));
    }

    @Test
    @DisplayName("Una competencia cuenta solo si su calificación más reciente es C")
    void cuentaCompetenciasEnC() {
        List<Calificacion> notas = List.of(
                nota(1, 1, 3, "C"), nota(1, 1, 4, "B"),
                nota(2, 1, 3, "A"), nota(2, 2, 6, "C"));

        assertEquals(1, servicio.competenciasEnC(notas));
    }

    @Test
    @DisplayName("Solo cuentan las incidencias graves de los últimos 60 días")
    void cuentaIncidenciasGraves() {
        List<Incidencia> incidencias = List.of(
                incidencia("Grave", HOY.minusDays(10)),
                incidencia("Moderada", HOY.minusDays(3)),
                incidencia("Grave", HOY.minusDays(90)));

        assertEquals(1, servicio.incidenciasGraves(incidencias, HOY));
    }

    @Test
    @DisplayName("El motivo explica las señales encontradas")
    void motivoConSenales() {
        assertEquals("3 faltas sin justificar en los últimos 30 días; 1 competencia en nivel C.",
                servicio.motivo(3, 1, 0));
        assertEquals("Sin alertas de asistencia, rendimiento ni conducta.", servicio.motivo(1, 0, 0));
    }

    @Test
    @DisplayName("Calcular guarda la predicción con nivel, motivo y fecha")
    void calcularGuarda() throws SQLException {
        when(asistenciaDAO.listarPorEstudiante(4)).thenReturn(List.of(
                falta(HOY, null), falta(HOY.minusDays(1), null), falta(HOY.minusDays(2), null)));
        when(incidenciaDAO.listarPorEstudiante(4)).thenReturn(List.of(incidencia("Grave", HOY)));

        PrediccionRiesgo p = servicio.calcular(4, HOY);

        assertEquals("Alto", p.getNivelRiesgo());
        assertEquals(HOY, p.getFechaCalculo());
        verify(prediccionDAO).insertar(p);
    }

    @Test
    @DisplayName("Calcular todos cuenta a los estudiantes por nivel")
    void calcularTodos() throws SQLException {
        Estudiante e1 = new Estudiante();
        e1.setIdEstudiante(1);
        Estudiante e2 = new Estudiante();
        e2.setIdEstudiante(2);
        when(estudianteDAO.listarTodos()).thenReturn(List.of(e1, e2));
        when(calificacionDAO.listarPorEstudiante(2)).thenReturn(List.of(nota(1, 1, 3, "C")));

        Map<String, Integer> conteo = servicio.calcularTodos(HOY);

        assertEquals(0, conteo.get("Alto"));
        assertEquals(1, conteo.get("Medio"));
        assertEquals(1, conteo.get("Bajo"));
        verify(prediccionDAO, times(2)).insertar(any());
    }

    private PrediccionRiesgoService conModelo(ClasificadorRiesgo modelo) {
        return new PrediccionRiesgoService(estudianteDAO, asistenciaDAO, calificacionDAO, incidenciaDAO,
                prediccionDAO, modelo);
    }

    @Test
    @DisplayName("Con modelo, el nivel lo decide el modelo y el motivo lo indica")
    void nivelDelModelo() throws SQLException {
        PrediccionRiesgo p = conModelo((faltas, enC, graves) -> "Alto").calcular(4, HOY);

        assertEquals("Alto", p.getNivelRiesgo());
        assertTrue(p.getMotivo().startsWith("Modelo J48: "));
        verify(prediccionDAO).insertar(p);
    }

    @Test
    @DisplayName("Si el modelo falla, se usan las reglas")
    void modeloFallaUsaReglas() throws SQLException {
        PrediccionRiesgo p = conModelo((faltas, enC, graves) -> {
            throw new IllegalStateException("Modelo no disponible");
        }).calcular(4, HOY);

        assertEquals("Bajo", p.getNivelRiesgo());
        assertTrue(p.getMotivo().startsWith("Reglas: "));
    }

    @Test
    @DisplayName("Si el modelo devuelve un valor fuera de la lista, se usan las reglas")
    void modeloValorInvalidoUsaReglas() {
        assertEquals(null, conModelo((faltas, enC, graves) -> "Crítico").nivelDelModelo(1, 0, 0));
        assertEquals(null, conModelo((faltas, enC, graves) -> null).nivelDelModelo(1, 0, 0));
        assertEquals("Medio", conModelo((faltas, enC, graves) -> "Medio").nivelDelModelo(1, 0, 0));
    }
}