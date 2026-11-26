package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas del catálogo de incidencias (RF-22),
 * con el DAO simulado en Mockito. No necesitan MySQL.
 */
class CatalogoIncidenciasTest {

    private TipoIncidenciaDAO dao;
    private CatalogoIncidencias catalogo;

    @BeforeEach
    void preparar() {
        dao = mock(TipoIncidenciaDAO.class);
        catalogo = new CatalogoIncidencias(dao);
    }

    private static TipoIncidencia tipo(int id, String nombre, boolean activo) {
        TipoIncidencia t = new TipoIncidencia();
        t.setIdTipoIncidencia(id);
        t.setNombre(nombre);
        t.setActivo(activo);
        return t;
    }

    @Test
    @DisplayName("Un tipo nuevo con nombre libre se registra")
    void registraTipoNuevo() throws SQLException {
        TipoIncidencia nuevo = tipo(0, "  Uso de celular  ", true);
        when(dao.existeNombre("Uso de celular", 0)).thenReturn(false);

        assertTrue(catalogo.guardar(nuevo));

        assertEquals("Uso de celular", nuevo.getNombre());
        verify(dao).registrarTipo(nuevo);
        verify(dao, never()).actualizar(any());
    }

    @Test
    @DisplayName("Un tipo existente se actualiza")
    void actualizaTipoExistente() throws SQLException {
        TipoIncidencia existente = tipo(3, "Tardanza reiterada", true);
        when(dao.existeNombre("Tardanza reiterada", 3)).thenReturn(false);

        assertFalse(catalogo.guardar(existente));

        verify(dao).actualizar(existente);
        verify(dao, never()).registrarTipo(any());
    }

    @Test
    @DisplayName("Un nombre repetido no se guarda")
    void nombreRepetidoNoSeGuarda() throws SQLException {
        TipoIncidencia repetido = tipo(0, "Pelea", true);
        when(dao.existeNombre("Pelea", 0)).thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> catalogo.guardar(repetido));

        assertEquals("Ya existe un tipo con este nombre.", error.getMessage());
        verify(dao, never()).registrarTipo(any());
        verify(dao, never()).actualizar(any());
    }

    @Test
    @DisplayName("Un nombre vacío no se guarda")
    void nombreVacioNoSeGuarda() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> catalogo.guardar(tipo(0, "   ", true)));
        assertThrows(IllegalArgumentException.class, () -> catalogo.guardar(tipo(0, null, true)));
        verify(dao, never()).registrarTipo(any());
    }

    @Test
    @DisplayName("El tipo \"Otra\" está bloqueado, sin importar mayúsculas")
    void otraEstaBloqueada() {
        assertTrue(catalogo.esBloqueado(tipo(1, "Otra", true)));
        assertTrue(catalogo.esBloqueado(tipo(1, "OTRA", true)));
        assertFalse(catalogo.esBloqueado(tipo(2, "Pelea", true)));
        assertFalse(catalogo.esBloqueado(null));
    }

    @Test
    @DisplayName("El tipo \"Otra\" no se puede modificar")
    void otraNoSeModifica() throws SQLException {
        assertThrows(IllegalStateException.class, () -> catalogo.guardar(tipo(1, "Otra", true)));
        verify(dao, never()).actualizar(any());
    }

    @Test
    @DisplayName("El tipo \"Otra\" no se puede desactivar")
    void otraNoSeDesactiva() throws SQLException {
        assertThrows(IllegalStateException.class, () -> catalogo.cambiarEstado(tipo(1, "Otra", true)));
        verify(dao, never()).cambiarEstado(anyInt(), anyBoolean());
    }

    @Test
    @DisplayName("Un tipo activo se desactiva y uno inactivo se activa")
    void cambiaEstado() throws SQLException {
        assertFalse(catalogo.cambiarEstado(tipo(4, "Pelea", true)));
        verify(dao).cambiarEstado(4, false);

        assertTrue(catalogo.cambiarEstado(tipo(5, "Uso de celular", false)));
        verify(dao).cambiarEstado(5, true);
    }
}
