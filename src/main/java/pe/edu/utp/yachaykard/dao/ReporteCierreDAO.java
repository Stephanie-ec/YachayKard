package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.ReporteCierre;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla reporte_cierre.
 */
public interface ReporteCierreDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param reporteCierre datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(ReporteCierre reporteCierre) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param reporteCierre datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(ReporteCierre reporteCierre) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idReporte id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idReporte) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idReporte id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    ReporteCierre buscarPorId(int idReporte) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<ReporteCierre> listarTodos() throws SQLException;

    /**
     * Genera el reporte de cierre de un estudiante (método generarReporte() del diagrama de clases).
     *
     * @param reporteCierre datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void generarReporte(ReporteCierre reporteCierre) throws SQLException {
        insertar(reporteCierre);
    }

    /**
     * Cierra el registro del periodo: marca como cerrados todos sus reportes
     * (método cerrarRegistro() del diagrama de clases).
     *
     * @param idPeriodo periodo que se cierra
     * @throws SQLException si ocurre un error en la base de datos
     */
    void cerrarRegistro(int idPeriodo) throws SQLException;

    /**
     * Lista los niveles finales de un estudiante por periodo.
     *
     * @param idEstudiante valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<ReporteCierre> listarPorEstudiante(int idEstudiante) throws SQLException;

    /**
     * Indica si el docente ya cerró el registro de un estudiante en una competencia y periodo.
     * La pantalla de calificaciones lo usa para bloquear la edición.
     *
     * @param idEstudiante  estudiante
     * @param idCompetencia competencia
     * @param idPeriodo     periodo
     * @return true si existe un reporte de cierre con estado "Cerrado"
     * @throws SQLException si ocurre un error en la base de datos
     */
    boolean estaCerrado(int idEstudiante, int idCompetencia, int idPeriodo) throws SQLException;
}