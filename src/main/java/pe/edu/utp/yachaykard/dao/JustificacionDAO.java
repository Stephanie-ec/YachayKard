package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Justificacion;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla justificacion.
 */
public interface JustificacionDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param justificacion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Justificacion justificacion) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param justificacion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Justificacion justificacion) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idJustificacion id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idJustificacion) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idJustificacion id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Justificacion buscarPorId(int idJustificacion) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Justificacion> listarTodos() throws SQLException;

    /**
     * Registra una justificación de inasistencia (método registrarJustificacion() del diagrama de clases).
     *
     * @param justificacion datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarJustificacion(Justificacion justificacion) throws SQLException {
        insertar(justificacion);
    }

    /**
     * Lista las justificaciones de un estudiante.
     *
     * @param idEstudiante valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Justificacion> listarPorEstudiante(int idEstudiante) throws SQLException;
}