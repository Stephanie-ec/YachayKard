package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.RolPermiso;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla rol_permiso.
 * Como la clave es compuesta, no hay actualizar(): para cambiar un permiso se quita y se asigna otro.
 */
public interface RolPermisoDAO {

    /**
     * Otorga un permiso a un rol.
     *
     * @param rolPermiso relación a guardar
     * @throws SQLException si ocurre un error o el rol ya tiene ese permiso
     */
    void insertar(RolPermiso rolPermiso) throws SQLException;

    /**
     * Quita un permiso a un rol.
     *
     * @param idRol     id del rol
     * @param idPermiso id del permiso
     * @throws SQLException si ocurre un error en la base de datos
     */
    void eliminar(int idRol, int idPermiso) throws SQLException;

    /**
     * Lista todas las relaciones rol-permiso.
     *
     * @return lista de relaciones (vacía si no hay registros)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<RolPermiso> listarTodos() throws SQLException;

    /**
     * Lista los permisos otorgados a un rol.
     *
     * @param idRol id del rol
     * @return relaciones de ese rol
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<RolPermiso> listarPorRol(int idRol) throws SQLException;

    /**
     * Indica si un rol tiene un permiso. Se usa para controlar el acceso a cada pantalla.
     *
     * @param idRol     id del rol
     * @param idPermiso id del permiso
     * @return true si el rol tiene el permiso
     * @throws SQLException si ocurre un error en la base de datos
     */
    boolean existe(int idRol, int idPermiso) throws SQLException;
}