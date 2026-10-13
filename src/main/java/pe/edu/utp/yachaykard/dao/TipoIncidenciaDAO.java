package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla tipo_incidencia.
 */
public interface TipoIncidenciaDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param tipoIncidencia datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(TipoIncidencia tipoIncidencia) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param tipoIncidencia datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(TipoIncidencia tipoIncidencia) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idTipoIncidencia id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idTipoIncidencia) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idTipoIncidencia id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    TipoIncidencia buscarPorId(int idTipoIncidencia) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<TipoIncidencia> listarTodos() throws SQLException;

    /**
     * Registra un tipo de incidencia en el catálogo (método registrarTipo() del diagrama de clases).
     *
     * @param tipoIncidencia datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarTipo(TipoIncidencia tipoIncidencia) throws SQLException {
        insertar(tipoIncidencia);
    }

    /**
     * Activa o desactiva un tipo. Los tipos no se eliminan (RF-22):
     * uno desactivado deja de ofrecerse al registrar incidencias.
     *
     * @param idTipoIncidencia id del tipo
     * @param activo           true para activar, false para desactivar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void cambiarEstado(int idTipoIncidencia, boolean activo) throws SQLException;

    /**
     * Lista solo los tipos activos (los que se ofrecen al registrar una incidencia).
     *
     * @return tipos activos
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<TipoIncidencia> listarActivos() throws SQLException;

    /**
     * Indica si ya existe otro tipo con el mismo nombre (sin distinguir mayúsculas).
     *
     * @param nombre    nombre a verificar
     * @param idExcluir id del tipo que se está editando (0 si es nuevo)
     * @return true si el nombre ya está en uso
     * @throws SQLException si ocurre un error en la base de datos
     */
    boolean existeNombre(String nombre, int idExcluir) throws SQLException;
}