package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AuxiliarDAO;
import pe.edu.utp.yachaykard.dao.DocenteDAO;
import pe.edu.utp.yachaykard.dao.UsuarioDAO;
import pe.edu.utp.yachaykard.modelo.Auxiliar;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Usuario;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de usuarios (RF-02), con los DAO simulados en Mockito. Datos ficticios.
 */
class GestionUsuariosTest {

    private UsuarioDAO usuarioDAO;
    private DocenteDAO docenteDAO;
    private AuxiliarDAO auxiliarDAO;
    private GestionUsuarios gestion;

    @BeforeEach
    void preparar() {
        usuarioDAO = mock(UsuarioDAO.class);
        docenteDAO = mock(DocenteDAO.class);
        auxiliarDAO = mock(AuxiliarDAO.class);
        gestion = new GestionUsuarios(usuarioDAO, docenteDAO, auxiliarDAO);
    }

    private static Usuario usuario(int id, String username, int idRol) {
        Usuario u = new Usuario();
        u.setIdUsuario(id);
        u.setUsername(username);
        u.setIdRol(idRol);
        u.setIdInstitucion(1);
        return u;
    }

    private static GestionUsuarios.DatosPersona persona() {
        GestionUsuarios.DatosPersona p = new GestionUsuarios.DatosPersona();
        p.setDni("40123456");
        p.setNombres("Rosa");
        p.setApellidos("Quispe Mamani");
        p.setEspecialidad("Matemática");
        return p;
    }

    @Test
    @DisplayName("Un administrador nuevo se registra sin datos personales")
    void registraAdministrador() throws SQLException {
        when(usuarioDAO.registrar(any())).thenReturn(10);

        gestion.registrar(usuario(0, "Director.Demo", 1), "Administrador", "Clave2026", null);

        verify(usuarioDAO).registrar(argThat(u -> "director.demo".equals(u.getUsername()) && u.isActivo()));
        verify(docenteDAO, never()).insertar(any());
        verify(auxiliarDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Al registrar un docente también se registran sus datos personales")
    void registraDocenteConPersona() throws SQLException {
        when(usuarioDAO.registrar(any())).thenReturn(11);

        gestion.registrar(usuario(0, "rquispe", 2), "Docente", "Clave2026", persona());

        verify(docenteDAO).insertar(argThat((Docente d) -> d.getIdUsuario() == 11 && "40123456".equals(d.getDni())));
    }

    @Test
    @DisplayName("Al registrar un auxiliar también se registran sus datos personales")
    void registraAuxiliarConPersona() throws SQLException {
        when(usuarioDAO.registrar(any())).thenReturn(12);

        gestion.registrar(usuario(0, "aux.lima", 3), "Auxiliar", "Clave2026", persona());

        verify(auxiliarDAO).insertar(argThat((Auxiliar a) -> a.getIdUsuario() == 12));
    }

    @Test
    @DisplayName("No se registra un usuario repetido")
    void usuarioRepetido() throws SQLException {
        when(usuarioDAO.buscarPorUsername("admin")).thenReturn(usuario(1, "admin", 1));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> gestion.registrar(usuario(0, "admin", 1), "Administrador", "Clave2026", null));

        assertEquals("Ya existe un usuario con ese nombre.", error.getMessage());
        verify(usuarioDAO, never()).registrar(any());
    }

    @Test
    @DisplayName("El nombre de usuario debe tener de 4 a 50 caracteres permitidos")
    void usernameInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> gestion.registrar(usuario(0, "ab", 1), "Administrador", "Clave2026", null));
        assertThrows(IllegalArgumentException.class,
                () -> gestion.registrar(usuario(0, "con espacio", 1), "Administrador", "Clave2026", null));
    }

    @Test
    @DisplayName("La contraseña necesita 8 caracteres, letras y números")
    void politicaDeClave() {
        assertThrows(IllegalArgumentException.class, () -> gestion.validarClave("Corta1"));
        assertThrows(IllegalArgumentException.class, () -> gestion.validarClave("SoloLetras"));
        assertThrows(IllegalArgumentException.class, () -> gestion.validarClave("12345678"));
        gestion.validarClave("Demo2026");
    }

    @Test
    @DisplayName("Un docente sin DNI válido no se registra")
    void docenteSinDni() throws SQLException {
        GestionUsuarios.DatosPersona p = persona();
        p.setDni("123");

        assertThrows(IllegalArgumentException.class,
                () -> gestion.registrar(usuario(0, "rquispe", 2), "Docente", "Clave2026", p));
        verify(usuarioDAO, never()).registrar(any());
    }

    @Test
    @DisplayName("Al editar sin contraseña nueva se conserva la actual")
    void editarSinClave() throws SQLException {
        Usuario u = usuario(5, "docente", 2);
        when(usuarioDAO.buscarPorUsername("docente")).thenReturn(u);

        gestion.actualizar(u, "");

        assertNull(u.getPassword());
        verify(usuarioDAO).actualizar(u);
    }

    @Test
    @DisplayName("Al editar no se puede usar el nombre de otro usuario")
    void editarConNombreAjeno() throws SQLException {
        when(usuarioDAO.buscarPorUsername("admin")).thenReturn(usuario(1, "admin", 1));

        assertThrows(IllegalArgumentException.class, () -> gestion.actualizar(usuario(5, "admin", 2), null));
        verify(usuarioDAO, never()).actualizar(any());
    }

    @Test
    @DisplayName("Un usuario activo se desactiva")
    void desactiva() throws SQLException {
        Usuario u = usuario(5, "docente", 2);

        assertFalse(gestion.cambiarEstado(u, 1));
        verify(usuarioDAO).cambiarEstado(5, false);
    }

    @Test
    @DisplayName("Nadie puede desactivar su propia cuenta")
    void noSeDesactivaASiMismo() throws SQLException {
        assertThrows(IllegalStateException.class, () -> gestion.cambiarEstado(usuario(1, "admin", 1), 1));
        verify(usuarioDAO, never()).cambiarEstado(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Solo Docente y Auxiliar piden datos personales")
    void requierePersona() {
        assertTrue(gestion.requierePersona("Docente"));
        assertTrue(gestion.requierePersona("Auxiliar"));
        assertFalse(gestion.requierePersona("Administrador"));
    }
}