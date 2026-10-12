package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Incidencia;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla incidencia.
 */
public interface IncidenciaDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param incidencia datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Incidencia incidencia) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param incidencia datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Incidencia incidencia) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idIncidencia id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idIncidencia) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idIncidencia id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Incidencia buscarPorId(int idIncidencia) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Incidencia> listarTodos() throws SQLException;

    /**
     * Registra una incidencia de conducta (método reportarIncidencia() del diagrama de clases).
     *
     * @param incidencia datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void reportarIncidencia(Incidencia incidencia) throws SQLException {
        insertar(incidencia);
    }

    /**
     * Lista las incidencias de un estudiante (historial y predicción de riesgo).
     *
     * @param idEstudiante valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Incidencia> listarPorEstudiante(int idEstudiante) throws SQLException;
}