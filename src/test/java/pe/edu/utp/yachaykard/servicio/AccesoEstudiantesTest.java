package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.DocenteDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de la regla de acceso al historial (RF-03), con los DAO simulados en Mockito.
 * No necesitan MySQL.
 */
class AccesoEstudiantesTest {

    private static final int ID_USUARIO_DOCENTE = 2;
    private static final int ID_DOCENTE = 1;
    private static final int ID_SECCION_TUTORIA = 1;

    private EstudianteDAO estudianteDAO;
    private DocenteDAO docenteDAO;
    private SeccionDAO seccionDAO;
    private AccesoEstudiantes acceso;
    private Usuario usuarioDocente;

    @BeforeEach
    void preparar() throws SQLException {
        estudianteDAO = mock(EstudianteDAO.class);
        docenteDAO = mock(DocenteDAO.class);
        seccionDAO = mock(SeccionDAO.class);
        acceso = new AccesoEstudiantes(estudianteDAO, docenteDAO, seccionDAO);

        usuarioDocente = new Usuario();
        usuarioDocente.setIdUsuario(ID_USUARIO_DOCENTE);

        Docente docente = new Docente();
        docente.setIdDocente(ID_DOCENTE);
        Seccion seccion = new Seccion();
        seccion.setIdSeccion(ID_SECCION_TUTORIA);

        when(estudianteDAO.listarTodos()).thenReturn(List.of(estudiante(4), estudiante(5)));
        when(docenteDAO.listarPorUsuario(ID_USUARIO_DOCENTE)).thenReturn(List.of(docente));
        when(seccionDAO.listarPorTutor(ID_DOCENTE)).thenReturn(List.of(seccion));
        when(estudianteDAO.listarPorSeccion(ID_SECCION_TUTORIA)).thenReturn(List.of(estudiante(4)));
    }

    private static Estudiante estudiante(int id) {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(id);
        return e;
    }

    @Test
    @DisplayName("El administrador ve a todos los estudiantes")
    void administradorVeATodos() throws SQLException {
        List<Estudiante> permitidos = acceso.permitidos(new Usuario(), DatosSesion.ADMINISTRADOR);

        assertEquals(2, permitidos.size());
        assertTrue(acceso.puedeConsultar(new Usuario(), DatosSesion.ADMINISTRADOR, 5));
    }

    @Test
    @DisplayName("El docente tutor ve solo a los estudiantes de su sección")
    void docenteTutorVeSuSeccion() throws SQLException {
        List<Estudiante> permitidos = acceso.permitidos(usuarioDocente, DatosSesion.DOCENTE);

        assertEquals(1, permitidos.size());
        assertEquals(4, permitidos.get(0).getIdEstudiante());
        assertTrue(acceso.puedeConsultar(usuarioDocente, DatosSesion.DOCENTE, 4));
        verify(estudianteDAO, never()).listarTodos();
    }

    @Test
    @DisplayName("El docente no puede ver a un estudiante de otra sección")
    void docenteNoVeOtraSeccion() throws SQLException {
        assertFalse(acceso.puedeConsultar(usuarioDocente, DatosSesion.DOCENTE, 5));
    }

    @Test
    @DisplayName("Un docente que no es tutor no ve a ningún estudiante")
    void docenteSinTutoriaNoVeANadie() throws SQLException {
        when(seccionDAO.listarPorTutor(anyInt())).thenReturn(List.of());

        assertTrue(acceso.permitidos(usuarioDocente, DatosSesion.DOCENTE).isEmpty());
        assertFalse(acceso.puedeConsultar(usuarioDocente, DatosSesion.DOCENTE, 4));
    }

    @Test
    @DisplayName("El auxiliar no tiene acceso al historial")
    void auxiliarNoTieneAcceso() throws SQLException {
        assertTrue(acceso.permitidos(usuarioDocente, DatosSesion.AUXILIAR).isEmpty());
        assertFalse(acceso.puedeConsultar(usuarioDocente, DatosSesion.AUXILIAR, 4));
    }
}
