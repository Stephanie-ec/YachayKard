package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Rol;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla rol.
 */
public interface RolDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param rol datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Rol rol) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param rol datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Rol rol) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idRol id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idRol) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idRol id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Rol buscarPorId(int idRol) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Rol> listarTodos() throws SQLException;

    /**
     * Crea un rol (método crearRol() del diagrama de clases).
     *
     * @param rol rol a crear
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void crearRol(Rol rol) throws SQLException {
        insertar(rol);
    }
}