package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Notificacion;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla notificacion.
 */
public interface NotificacionDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param notificacion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Notificacion notificacion) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param notificacion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Notificacion notificacion) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idNotificacion id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idNotificacion) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idNotificacion id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Notificacion buscarPorId(int idNotificacion) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Notificacion> listarTodos() throws SQLException;

    /**
     * Registra una notificación (método enviarNotificacion() del diagrama de clases).
     *
     * @param notificacion datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void enviarNotificacion(Notificacion notificacion) throws SQLException {
        insertar(notificacion);
    }

    /**
     * Lista las notificaciones internas de un usuario (bandeja del tutor o del administrador).
     *
     * @param idUsuario valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Notificacion> listarPorUsuario(int idUsuario) throws SQLException;
}