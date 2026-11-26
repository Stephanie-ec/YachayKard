package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.JustificacionDAO;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Justificacion;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del registro de asistencia (RF-18) y de la justificación de un día (RF-19),
 * con los DAO simulados en Mockito.
 */
class RegistroAsistenciaTest {

    /** Lunes 16 de noviembre de 2026. */
    private static final LocalDate LUNES = LocalDate.of(2026, 11, 16);

    private AsistenciaDAO asistenciaDAO;
    private JustificacionDAO justificacionDAO;
    private RegistroAsistencia registro;

    @BeforeEach
    void preparar() {
        asistenciaDAO = mock(AsistenciaDAO.class);
        justificacionDAO = mock(JustificacionDAO.class);
        registro = new RegistroAsistencia(asistenciaDAO, justificacionDAO);
    }

    private static Asistencia asistencia(int id, int idEstudiante, String estado, Integer idJustificacion) {
        Asistencia a = new Asistencia();
        a.setIdAsistencia(id);
        a.setIdEstudiante(idEstudiante);
        a.setEstado(estado);
        a.setFecha(LUNES);
        a.setIdJustificacion(idJustificacion);
        return a;
    }

    @Test
    @DisplayName("Los registros nuevos se insertan y los existentes se actualizan")
    void insertaYActualiza() throws SQLException {
        Asistencia nueva = asistencia(0, 4, "Presente", null);
        Asistencia existente = asistencia(7, 5, "Tardanza", null);

        registro.guardar(List.of(nueva, existente), LUNES, LUNES, 1);

        verify(asistenciaDAO).insertar(nueva);
        verify(asistenciaDAO).actualizar(existente);
        assertEquals(1, nueva.getIdAuxiliar());
    }

    @Test
    @DisplayName("No se registra asistencia de una fecha futura ni de fin de semana")
    void fechaInvalida() throws SQLException {
        List<Asistencia> lista = List.of(asistencia(0, 4, "Presente", null));

        assertThrows(IllegalArgumentException.class, () -> registro.guardar(lista, LUNES.plusDays(1), LUNES, 1));
        assertThrows(IllegalArgumentException.class, () -> registro.guardar(lista, LUNES.minusDays(1), LUNES, 1));
        verify(asistenciaDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Un estado distinto de Presente, Tardanza o Falta no se guarda")
    void estadoInvalido() throws SQLException {
        assertThrows(IllegalArgumentException.class,
                () -> registro.guardar(List.of(asistencia(0, 4, "Permiso", null)), LUNES, LUNES, 1));
        verify(asistenciaDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Sin auxiliar asignado no se guarda")
    void sinAuxiliar() {
        assertThrows(IllegalArgumentException.class,
                () -> registro.guardar(List.of(asistencia(0, 4, "Presente", null)), LUNES, LUNES, 0));
    }

    @Test
    @DisplayName("Si una falta justificada se corrige a Presente, deja de estar justificada")
    void correccionQuitaJustificacion() throws SQLException {
        Asistencia corregida = asistencia(7, 4, "Presente", 3);

        registro.guardar(List.of(corregida), LUNES, LUNES, 1);

        assertNull(corregida.getIdJustificacion());
    }

    @Test
    @DisplayName("Justificar una falta crea la justificación de un día y la enlaza")
    void justificaFalta() throws SQLException {
        Asistencia falta = asistencia(7, 4, "Falta", null);
        when(justificacionDAO.registrar(any())).thenReturn(12);

        registro.justificar(falta, "  Cita médica  ", 2);

        verify(justificacionDAO).registrar(argThat((Justificacion j) -> LUNES.equals(j.getFechaInicio())
                && LUNES.equals(j.getFechaFin()) && "Cita médica".equals(j.getMotivo()) && j.getIdEstudiante() == 4));
        assertEquals(12, falta.getIdJustificacion());
        verify(asistenciaDAO).actualizar(falta);
    }

    @Test
    @DisplayName("Solo se justifica una falta guardada y sin justificar")
    void soloFaltasPendientes() throws SQLException {
        assertThrows(IllegalStateException.class,
                () -> registro.justificar(asistencia(7, 4, "Tardanza", null), "Cita médica", 2));
        assertThrows(IllegalStateException.class,
                () -> registro.justificar(asistencia(0, 4, "Falta", null), "Cita médica", 2));
        assertThrows(IllegalStateException.class,
                () -> registro.justificar(asistencia(7, 4, "Falta", 3), "Cita médica", 2));
        verify(justificacionDAO, never()).registrar(any());
    }

    @Test
    @DisplayName("El motivo debe tener al menos 5 caracteres")
    void motivoCorto() throws SQLException {
        assertThrows(IllegalArgumentException.class,
                () -> registro.justificar(asistencia(7, 4, "Falta", null), "tos", 2));
        verify(justificacionDAO, never()).registrar(any());
    }
}
