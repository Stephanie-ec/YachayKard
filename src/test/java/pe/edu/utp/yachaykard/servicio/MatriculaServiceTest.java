package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.MatriculaDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.modelo.Matricula;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
 * Pruebas de las reglas de matrícula (RF-12): una matrícula activa por año y aforo de la sección,
 * con los DAO simulados en Mockito. No necesitan MySQL.
 */
class MatriculaServiceTest {

    private MatriculaDAO matriculaDAO;
    private SeccionDAO seccionDAO;
    private MatriculaService servicio;
    private Matricula matricula;

    @BeforeEach
    void preparar() throws SQLException {
        matriculaDAO = mock(MatriculaDAO.class);
        seccionDAO = mock(SeccionDAO.class);
        servicio = new MatriculaService(matriculaDAO, seccionDAO);

        matricula = matricula(4, MatriculaService.ACTIVA);

        Seccion seccion = new Seccion();
        seccion.setIdSeccion(1);
        seccion.setAforo(2);
        when(seccionDAO.buscarPorId(1)).thenReturn(seccion);
        when(matriculaDAO.listarPorSeccion(1)).thenReturn(new ArrayList<>());
    }

    private static Matricula matricula(int idEstudiante, String estado) {
        Matricula m = new Matricula();
        m.setFechaMatricula(LocalDate.of(2026, 3, 2));
        m.setAnioLectivo("2026");
        m.setEstado(estado);
        m.setIdEstudiante(idEstudiante);
        m.setIdSeccion(1);
        return m;
    }

    @Test
    @DisplayName("Si el estudiante no tiene matrícula activa y hay vacantes, se registra")
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
    @DisplayName("Si la sección completó su aforo, no se registra")
    void noRegistraSiSeccionLlena() throws SQLException {
        when(matriculaDAO.listarPorSeccion(1)).thenReturn(List.of(
                matricula(1, "Activa"), matricula(2, "Activa"), matricula(3, "Retirado")));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> servicio.registrar(matricula));

        assertEquals("La sección ya completó su aforo de 2 estudiantes.", error.getMessage());
        verify(matriculaDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Una matrícula retirada no ocupa vacante")
    void retiradosNoOcupanVacante() throws SQLException {
        when(matriculaDAO.listarPorSeccion(1)).thenReturn(List.of(matricula(1, "Activa"), matricula(3, "Retirado")));

        servicio.registrar(matricula);

        verify(matriculaDAO).insertar(matricula);
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

    @Test
    @DisplayName("Retirar una matrícula activa la actualiza sin revisar vacantes")
    void retirar() throws SQLException {
        servicio.cambiarEstado(matricula, MatriculaService.RETIRADO);

        assertEquals("Retirado", matricula.getEstado());
        verify(matriculaDAO).actualizar(matricula);
        verify(matriculaDAO, never()).existeMatriculaActiva(anyInt(), anyString());
    }

    @Test
    @DisplayName("No se reactiva una matrícula si el estudiante ya tiene otra activa")
    void noReactivaSiHayOtraActiva() throws SQLException {
        Matricula retirada = matricula(4, MatriculaService.RETIRADO);
        when(matriculaDAO.existeMatriculaActiva(4, "2026")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> servicio.cambiarEstado(retirada, MatriculaService.ACTIVA));
        verify(matriculaDAO, never()).actualizar(any());
    }
}
