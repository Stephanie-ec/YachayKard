package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Sesion;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla sesion.
 */
public interface SesionDAO {

    /**
     * Inserta una nueva sesión.
     *
     * @param sesion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Sesion sesion) throws SQLException;

    /**
     * Actualiza una sesión existente.
     *
     * @param sesion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Sesion sesion) throws SQLException;

    /**
     * Elimina una sesión por su id.
     *
     * @param idSesion id de la sesión
     * @throws SQLException si ocurre un error en la base de datos
     */
    void eliminar(int idSesion) throws SQLException;

    /**
     * Busca una sesión por su id.
     *
     * @param idSesion id de la sesión
     * @return la sesión encontrada o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Sesion buscarPorId(int idSesion) throws SQLException;

    /**
     * Lista todas las sesiones registradas.
     *
     * @return lista de sesiones (vacía si no hay registros)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Sesion> listarTodos() throws SQLException;

    /**
     * Registra el inicio de sesión de un usuario con la fecha y hora actual
     * (método iniciarSesion() del diagrama de clases).
     *
     * @param idUsuario usuario que inicia sesión
     * @return id generado de la nueva sesión
     * @throws SQLException si ocurre un error en la base de datos
     */
    int iniciarSesion(int idUsuario) throws SQLException;

    /**
     * Registra la fecha y hora de cierre de una sesión abierta
     * (método cerrarSesion() del diagrama de clases).
     *
     * @param idSesion sesión que se cierra
     * @throws SQLException si ocurre un error en la base de datos
     */
    void cerrarSesion(int idSesion) throws SQLException;
}