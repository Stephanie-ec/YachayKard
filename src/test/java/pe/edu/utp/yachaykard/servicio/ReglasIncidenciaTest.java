package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del registro de incidencias (RF-23), con los DAO simulados en Mockito.
 */
class ReglasIncidenciaTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 16, 10, 30);
    private static final int TARDANZA = 1;
    private static final int OTRA = 2;
    private static final int INACTIVO = 3;

    private IncidenciaDAO incidenciaDAO;
    private ReglasIncidencia reglas;
    private Incidencia incidencia;

    @BeforeEach
    void preparar() throws SQLException {
        incidenciaDAO = mock(IncidenciaDAO.class);
        TipoIncidenciaDAO tipoDAO = mock(TipoIncidenciaDAO.class);
        reglas = new ReglasIncidencia(incidenciaDAO, tipoDAO);
        when(tipoDAO.buscarPorId(TARDANZA)).thenReturn(tipo(TARDANZA, "Tardanza reiterada", true));
        when(tipoDAO.buscarPorId(OTRA)).thenReturn(tipo(OTRA, "Otra", true));
        when(tipoDAO.buscarPorId(INACTIVO)).thenReturn(tipo(INACTIVO, "Uso de celular", false));

        incidencia = new Incidencia();
        incidencia.setIdEstudiante(4);
        incidencia.setIdTipoIncidencia(TARDANZA);
        incidencia.setGravedad("Leve");
        incidencia.setAccionTomada("Diálogo con el estudiante");
        incidencia.setIdUsuario(2);
    }

    private static TipoIncidencia tipo(int id, String nombre, boolean activo) {
        TipoIncidencia t = new TipoIncidencia();
        t.setIdTipoIncidencia(id);
        t.setNombre(nombre);
        t.setActivo(activo);
        return t;
    }

    @Test
    @DisplayName("Una incidencia leve con acción se registra con la hora del sistema")
    void registraLeve() throws SQLException {
        reglas.registrar(incidencia, AHORA);

        assertEquals(AHORA, incidencia.getFechaHora());
        assertFalse(incidencia.isRequiereSiseve());
        assertNull(incidencia.getDescripcion());
        verify(incidenciaDAO).insertar(incidencia);
    }

    @Test
    @DisplayName("Sin acción tomada no se registra")
    void accionObligatoria() throws SQLException {
        incidencia.setAccionTomada(null);

        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(incidencia, AHORA));
        verify(incidenciaDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Un tipo desactivado no se puede usar")
    void tipoInactivo() {
        incidencia.setIdTipoIncidencia(INACTIVO);
        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(incidencia, AHORA));
    }

    @Test
    @DisplayName("El tipo \"Otra\" exige describir lo ocurrido")
    void otraPideDescripcion() throws SQLException {
        incidencia.setIdTipoIncidencia(OTRA);
        incidencia.setDescripcion("corto");
        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(incidencia, AHORA));

        incidencia.setDescripcion("Dañó la carpeta del aula.");
        reglas.registrar(incidencia, AHORA);
        verify(incidenciaDAO).insertar(incidencia);
    }

    @Test
    @DisplayName("Una incidencia grave exige descripción y se marca para el SíseVe")
    void graveSeMarcaSiseve() throws SQLException {
        incidencia.setGravedad("Grave");
        incidencia.setDescripcion("Agresión física durante el recreo.");

        reglas.registrar(incidencia, AHORA);

        assertTrue(incidencia.isRequiereSiseve());
        verify(incidenciaDAO).insertar(incidencia);
    }

    @Test
    @DisplayName("Una gravedad fuera de la lista no se registra")
    void gravedadInvalida() {
        incidencia.setGravedad("Muy grave");
        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(incidencia, AHORA));
    }

    @Test
    @DisplayName("Las acciones caben en la columna de 50 caracteres")
    void accionesCaben() {
        ReglasIncidencia.ACCIONES.forEach(a -> assertTrue(a.length() <= 50));
    }
}
