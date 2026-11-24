package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

import java.sql.SQLException;

/**
 * Reglas del catálogo de tipos de incidencia (RF-22):
 * <ul>
 *     <li>El nombre es obligatorio y no se puede repetir.</li>
 *     <li>El tipo "Otra" no se edita ni se desactiva: siempre queda disponible.</li>
 *     <li>Los tipos no se eliminan; solo se activan o desactivan.</li>
 * </ul>
 * El DAO se recibe en el constructor para poder simularlo con Mockito en las pruebas.
 */
public class CatalogoIncidencias {

    /** Tipo que no se puede editar ni desactivar. */
    public static final String TIPO_OTRA = "Otra";

    private final TipoIncidenciaDAO tipoIncidenciaDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public CatalogoIncidencias() {
        this(new TipoIncidenciaDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param tipoIncidenciaDAO acceso al catálogo
     */
    public CatalogoIncidencias(TipoIncidenciaDAO tipoIncidenciaDAO) {
        this.tipoIncidenciaDAO = tipoIncidenciaDAO;
    }

    /**
     * @param tipo tipo a revisar
     * @return true si es el tipo "Otra", que no se edita ni se desactiva
     */
    public boolean esBloqueado(TipoIncidencia tipo) {
        return tipo != null && TIPO_OTRA.equalsIgnoreCase(tipo.getNombre());
    }

    /**
     * Registra un tipo nuevo (id 0) o actualiza uno existente.
     *
     * @param tipo datos del formulario
     * @return true si se registró uno nuevo; false si se actualizó
     * @throws IllegalArgumentException si el nombre está vacío o ya existe
     * @throws IllegalStateException    si se intenta modificar el tipo "Otra"
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public boolean guardar(TipoIncidencia tipo) throws SQLException {
        String nombre = tipo.getNombre() == null ? "" : tipo.getNombre().trim();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo es obligatorio.");
        }
        tipo.setNombre(nombre);
        boolean nuevo = tipo.getIdTipoIncidencia() == 0;
        if (!nuevo && esBloqueado(tipo)) {
            throw new IllegalStateException("El tipo \"Otra\" no se puede modificar.");
        }
        if (tipoIncidenciaDAO.existeNombre(nombre, tipo.getIdTipoIncidencia())) {
            throw new IllegalArgumentException("Ya existe un tipo con este nombre.");
        }
        if (nuevo) {
            tipoIncidenciaDAO.registrarTipo(tipo);
        } else {
            tipoIncidenciaDAO.actualizar(tipo);
        }
        return nuevo;
    }

    /**
     * Activa un tipo desactivado o desactiva uno activo.
     *
     * @param tipo tipo elegido en la tabla
     * @return true si quedó activo; false si quedó desactivado
     * @throws IllegalStateException si es el tipo "Otra"
     * @throws SQLException          si ocurre un error en la base de datos
     */
    public boolean cambiarEstado(TipoIncidencia tipo) throws SQLException {
        if (esBloqueado(tipo)) {
            throw new IllegalStateException("El tipo \"Otra\" siempre debe estar disponible.");
        }
        boolean activar = !tipo.isActivo();
        tipoIncidenciaDAO.cambiarEstado(tipo.getIdTipoIncidencia(), activar);
        return activar;
    }
}
