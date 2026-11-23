package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.modelo.ReporteCierre;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del cierre del periodo (RF-30): nivel sugerido por predominancia y conclusión descriptiva.
 */
class CierrePeriodoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 20);

    private ReporteCierreDAO dao;
    private CierrePeriodo cierre;

    @BeforeEach
    void preparar() {
        dao = mock(ReporteCierreDAO.class);
        cierre = new CierrePeriodo(dao);
    }

    private static ReporteCierre reporte(int idEstudiante, String nivel, String conclusion) {
        ReporteCierre r = new ReporteCierre();
        r.setIdEstudiante(idEstudiante);
        r.setIdCompetencia(1);
        r.setIdPeriodo(2);
        r.setNivelFinal(nivel);
        r.setConclusionDescriptiva(conclusion);
        return r;
    }

    @Test
    @DisplayName("Se sugiere el nivel más frecuente: si hay más A, sale A")
    void sugiereElMasFrecuente() {
        assertEquals("A", cierre.sugerirNivel(List.of("A", "B", "A")));
        assertEquals("B", cierre.sugerirNivel(List.of("B", "B", "AD")));
    }

    @Test
    @DisplayName("En empate se sugiere el nivel del último mes")
    void empateUltimoMes() {
        assertEquals("A", cierre.sugerirNivel(List.of("B", "A")));
        assertEquals("C", cierre.sugerirNivel(List.of("C", "B", "B", "C")));
    }

    @Test
    @DisplayName("Los meses sin nota no cuentan; sin notas no hay sugerencia")
    void mesesSinNota() {
        assertEquals("B", cierre.sugerirNivel(Arrays.asList("B", null, null)));
        assertNull(cierre.sugerirNivel(Arrays.asList(null, null)));
        assertNull(cierre.sugerirNivel(List.of()));
    }

    @Test
    @DisplayName("En C la conclusión es obligatoria; si se escribe, mínimo 10 caracteres")
    void validaConclusion() {
        assertEquals("En el nivel C la conclusión descriptiva es obligatoria.", cierre.validar("C", "  "));
        assertEquals("La conclusión descriptiva debe tener al menos 10 caracteres.", cierre.validar("A", "Bien"));
        assertNull(cierre.validar("C", "Necesita reforzar fracciones."));
        assertNull(cierre.validar("AD", null));
        assertEquals("Elija el nivel final (AD, A, B o C).", cierre.validar("D", null));
    }

    @Test
    @DisplayName("Cerrar guarda los niveles con estado Cerrado y la fecha del cierre")
    void cerrarGuarda() throws SQLException {
        ReporteCierre r = reporte(4, "A", null);

        assertTrue(cierre.cerrar(List.of(r), HOY).isEmpty());

        assertEquals("Cerrado", r.getEstado());
        assertEquals(HOY, r.getFechaGeneracion());
        verify(dao).insertar(r);
    }

    @Test
    @DisplayName("Si un estudiante tiene error, no se guarda ninguno")
    void conErrorNoGuardaNada() throws SQLException {
        List<String> errores = cierre.cerrar(List.of(reporte(4, "A", null), reporte(5, "C", "")), HOY);

        assertEquals(List.of("Estudiante N.° 2: En el nivel C la conclusión descriptiva es obligatoria."), errores);
        verify(dao, never()).insertar(any());
        verify(dao, never()).actualizar(any());
    }

    @Test
    @DisplayName("Si ya existía un registro abierto, se actualiza en lugar de insertar")
    void actualizaExistente() throws SQLException {
        ReporteCierre existente = reporte(4, "B", null);
        existente.setIdReporte(9);
        existente.setEstado("Abierto");
        when(dao.buscar(4, 1, 2)).thenReturn(existente);
        ReporteCierre r = reporte(4, "A", null);

        cierre.cerrar(List.of(r), HOY);

        assertEquals(9, r.getIdReporte());
        verify(dao).actualizar(r);
        verify(dao, never()).insertar(any());
    }
}