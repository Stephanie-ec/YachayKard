package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Calificacion;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla calificacion.
 */
public interface CalificacionDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param calificacion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Calificacion calificacion) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param calificacion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Calificacion calificacion) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idCalificacion id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idCalificacion) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idCalificacion id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Calificacion buscarPorId(int idCalificacion) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Calificacion> listarTodos() throws SQLException;

    /**
     * Registra el nivel de logro mensual de un estudiante (método registrarNota() del diagrama de clases).
     *
     * @param calificacion datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarNota(Calificacion calificacion) throws SQLException {
        insertar(calificacion);
    }

    /**
     * Lista las calificaciones de un estudiante (se usa en el historial).
     *
     * @param idEstudiante valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Calificacion> listarPorEstudiante(int idEstudiante) throws SQLException;
}