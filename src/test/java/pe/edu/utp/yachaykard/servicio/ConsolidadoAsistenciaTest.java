package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.modelo.Asistencia;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del consolidado mensual de asistencia (RF-21),
 * con el DAO simulado en Mockito. Datos ficticios de octubre de 2026.
 */
class ConsolidadoAsistenciaTest {

    private AsistenciaDAO dao;
    private ConsolidadoAsistencia consolidado;

    @BeforeEach
    void preparar() {
        dao = mock(AsistenciaDAO.class);
        consolidado = new ConsolidadoAsistencia(dao);
    }

    private static Asistencia registro(int idEstudiante, int dia, String estado, Integer idJustificacion) {
        Asistencia a = new Asistencia();
        a.setIdEstudiante(idEstudiante);
        a.setFecha(LocalDate.of(2026, 10, dia));
        a.setEstado(estado);
        a.setIdJustificacion(idJustificacion);
        return a;
    }

    @Test
    @DisplayName("Cuenta presentes, tardanzas y faltas de cada estudiante")
    void cuentaPorEstudiante() throws SQLException {
        when(dao.listarPorSeccionYMes(1, 2026, 10)).thenReturn(List.of(
                registro(4, 5, "Presente", null),
                registro(4, 6, "Presente", null),
                registro(4, 7, "Tardanza", null),
                registro(5, 5, "Presente", null),
                registro(5, 6, "Falta", 1),
                registro(5, 7, "Falta", null)));

        List<ResumenAsistencia> filas = consolidado.generar(1, 2026, 10);

        assertEquals(2, filas.size());
        ResumenAsistencia mateo = filas.get(0);
        assertEquals(4, mateo.getIdEstudiante());
        assertEquals(2, mateo.getPresentes());
        assertEquals(1, mateo.getTardanzas());
        assertEquals(0, mateo.getFaltas());
        assertEquals(100.0, mateo.getPorcentajeAsistencia());

        ResumenAsistencia sofia = filas.get(1);
        assertEquals(1, sofia.getPresentes());
        assertEquals(1, sofia.getFaltas());
        assertEquals(1, sofia.getFaltasJustificadas());
        assertEquals(3, sofia.getDiasRegistrados());
        assertEquals(33.3, sofia.getPorcentajeAsistencia());
    }

    @Test
    @DisplayName("Una falta con justificación cuenta como justificada, no como falta")
    void faltaJustificada() {
        List<ResumenAsistencia> filas = consolidado.consolidar(List.of(registro(4, 6, "Falta", 9)));

        assertEquals(0, filas.get(0).getFaltas());
        assertEquals(1, filas.get(0).getFaltasJustificadas());
    }

    @Test
    @DisplayName("Un mes sin registros da un consolidado vacío")
    void mesSinRegistros() throws SQLException {
        when(dao.listarPorSeccionYMes(1, 2026, 12)).thenReturn(List.of());

        assertTrue(consolidado.generar(1, 2026, 12).isEmpty());
    }

    @Test
    @DisplayName("Un mes fuera de 1 a 12 se rechaza sin consultar la base de datos")
    void mesInvalido() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> consolidado.generar(1, 2026, 13));
        assertThrows(IllegalArgumentException.class, () -> consolidado.generar(1, 2026, 0));
        verify(dao, never()).listarPorSeccionYMes(anyInt(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Sin días registrados el porcentaje es 0")
    void porcentajeSinDias() {
        assertEquals(0.0, new ResumenAsistencia(4).getPorcentajeAsistencia());
    }
}
