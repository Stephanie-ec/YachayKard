package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.MatriculaDAO;
import pe.edu.utp.yachaykard.modelo.Matricula;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de la regla "una matrícula activa por año lectivo" (RF-12),
 * con el DAO simulado en Mockito. No necesitan MySQL.
 */
class MatriculaServiceTest {

    private MatriculaDAO matriculaDAO;
    private MatriculaService servicio;
    private Matricula matricula;

    @BeforeEach
    void preparar() {
        matriculaDAO = mock(MatriculaDAO.class);
        servicio = new MatriculaService(matriculaDAO);

        matricula = new Matricula();
        matricula.setFechaMatricula(LocalDate.of(2026, 3, 2));
        matricula.setAnioLectivo("2026");
        matricula.setEstado(MatriculaService.ACTIVA);
        matricula.setIdEstudiante(4);
        matricula.setIdSeccion(1);
    }

    @Test
    @DisplayName("Si el estudiante no tiene matrícula activa, se registra")
    void registraSiNoHayActiva() throws SQLException {
        when(matriculaDAO.existeMatriculaActiva(4, "2026")).thenReturn(false);

        servicio.registrar(matricula);

        verify(matriculaDAO).insertar(matricula);
    }

    @Test
    @DisplayName("Si ya tiene una matrícula activa ese año, no se registra")
    void noRegistraSiYaHayActiva() throws SQLException {
        when(matriculaDAO.existeMatriculaActiva(4, "2026")).thenReturn(true);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicio.registrar(matricula));

        assertEquals("El estudiante ya tiene una matrícula activa en el año lectivo 2026.", error.getMessage());
        verify(matriculaDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Con datos incompletos no se consulta ni se guarda nada")
    void datosIncompletosNoLleganAlDAO() throws SQLException {
        matricula.setIdSeccion(0);

        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(matricula));

        verify(matriculaDAO, never()).existeMatriculaActiva(anyInt(), anyString());
        verify(matriculaDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("El año lectivo debe tener 4 dígitos")
    void anioLectivoInvalido() {
        matricula.setAnioLectivo("26");
        assertThrows(IllegalArgumentException.class, () -> servicio.validarDatos(matricula));
    }

    @Test
    @DisplayName("Solo se aceptan los estados Activa, Retirado o Trasladado")
    void estadoInvalido() {
        matricula.setEstado("Suspendido");
        assertThrows(IllegalArgumentException.class, () -> servicio.validarDatos(matricula));
    }
}
