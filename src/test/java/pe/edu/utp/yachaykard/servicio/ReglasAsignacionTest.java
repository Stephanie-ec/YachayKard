package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AsignacionCursoDAO;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de asignación docente – curso – sección (RF-08),
 * con los DAO simulados en Mockito.
 */
class ReglasAsignacionTest {

    private static final int MATEMATICA = 1;
    private static final int TIC = 11;
    private static final int TUTOR = 1;
    private static final int OTRO_DOCENTE = 2;

    private AsignacionCursoDAO asignacionDAO;
    private CursoDAO cursoDAO;
    private SeccionDAO seccionDAO;
    private ReglasAsignacion reglas;

    @BeforeEach
    void preparar() throws SQLException {
        asignacionDAO = mock(AsignacionCursoDAO.class);
        cursoDAO = mock(CursoDAO.class);
        seccionDAO = mock(SeccionDAO.class);
        reglas = new ReglasAsignacion(asignacionDAO, cursoDAO, seccionDAO);

        when(cursoDAO.buscarPorId(MATEMATICA)).thenReturn(curso(MATEMATICA, "063"));
        when(cursoDAO.buscarPorId(TIC)).thenReturn(curso(TIC, "0006"));
        Seccion primeroA = new Seccion();
        primeroA.setIdSeccion(1);
        primeroA.setIdDocenteTutor(TUTOR);
        when(seccionDAO.buscarPorId(1)).thenReturn(primeroA);
        when(asignacionDAO.listarTodos()).thenReturn(List.of());
    }

    private static Curso curso(int id, String codigo) {
        Curso c = new Curso();
        c.setIdCurso(id);
        c.setCodigoSiagie(codigo);
        return c;
    }

    private static AsignacionCurso asignacion(int idDocente, int idCurso) {
        AsignacionCurso a = new AsignacionCurso();
        a.setIdDocente(idDocente);
        a.setIdCurso(idCurso);
        a.setIdSeccion(1);
        a.setAnioAcademico("2026");
        return a;
    }

    @Test
    @DisplayName("Una asignación libre se registra")
    void registraAsignacionLibre() throws SQLException {
        AsignacionCurso nueva = asignacion(OTRO_DOCENTE, MATEMATICA);

        reglas.registrar(nueva);

        verify(asignacionDAO).insertar(nueva);
    }

    @Test
    @DisplayName("Un curso no puede tener dos docentes en la misma sección y año")
    void cursoYaAsignado() throws SQLException {
        when(asignacionDAO.listarTodos()).thenReturn(List.of(asignacion(TUTOR, MATEMATICA)));
        AsignacionCurso duplicada = asignacion(OTRO_DOCENTE, MATEMATICA);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> reglas.registrar(duplicada));

        assertEquals("Ese curso ya tiene un docente asignado en la sección este año.", error.getMessage());
        verify(asignacionDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("El mismo curso en otro año sí se puede asignar")
    void otroAnio() throws SQLException {
        AsignacionCurso anterior = asignacion(TUTOR, MATEMATICA);
        anterior.setAnioAcademico("2025");
        when(asignacionDAO.listarTodos()).thenReturn(List.of(anterior));

        reglas.registrar(asignacion(OTRO_DOCENTE, MATEMATICA));

        verify(asignacionDAO).insertar(any());
    }

    @Test
    @DisplayName("Las transversales (Tutoría) solo se asignan al tutor de la sección")
    void transversalSoloAlTutor() throws SQLException {
        AsignacionCurso noTutor = asignacion(OTRO_DOCENTE, TIC);
        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(noTutor));

        AsignacionCurso alTutor = asignacion(TUTOR, TIC);
        reglas.registrar(alTutor);
        verify(asignacionDAO).insertar(alTutor);
    }

    @Test
    @DisplayName("Faltan datos o el año no tiene 4 dígitos")
    void datosIncompletos() {
        AsignacionCurso sinDocente = asignacion(0, MATEMATICA);
        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(sinDocente));

        AsignacionCurso anioMalo = asignacion(TUTOR, MATEMATICA);
        anioMalo.setAnioAcademico("26");
        assertThrows(IllegalArgumentException.class, () -> reglas.registrar(anioMalo));
    }
}