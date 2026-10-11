package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Permiso;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla permiso.
 */
public interface PermisoDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param permiso datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Permiso permiso) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param permiso datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Permiso permiso) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idPermiso id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idPermiso) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idPermiso id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Permiso buscarPorId(int idPermiso) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Permiso> listarTodos() throws SQLException;

    /**
     * Verifica si un rol tiene un permiso, buscándolo por su nombre
     * (método verificar() del diagrama de clases).
     *
     * @param idRol         rol a consultar
     * @param nombrePermiso nombre del permiso
     * @return true si el rol tiene el permiso
     * @throws SQLException si ocurre un error en la base de datos
     */
    boolean verificar(int idRol, String nombrePermiso) throws SQLException;
}