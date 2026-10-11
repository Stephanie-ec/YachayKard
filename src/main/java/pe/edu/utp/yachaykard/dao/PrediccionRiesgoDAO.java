package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla prediccion_riesgo.
 * El cálculo del nivel (calcularRiesgo) lo hace PrediccionRiesgoService; este DAO solo guarda y consulta.
 */
public interface PrediccionRiesgoDAO {

    /**
     * Inserta una nueva predicción.
     *
     * @param prediccion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(PrediccionRiesgo prediccion) throws SQLException;

    /**
     * Actualiza una predicción existente.
     *
     * @param prediccion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(PrediccionRiesgo prediccion) throws SQLException;

    /**
     * Elimina una predicción por su id.
     *
     * @param idPrediccion id de la predicción
     * @throws SQLException si ocurre un error en la base de datos
     */
    void eliminar(int idPrediccion) throws SQLException;

    /**
     * Busca una predicción por su id.
     *
     * @param idPrediccion id de la predicción
     * @return la predicción encontrada o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    PrediccionRiesgo buscarPorId(int idPrediccion) throws SQLException;

    /**
     * Lista todas las predicciones registradas.
     *
     * @return lista de predicciones (vacía si no hay registros)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<PrediccionRiesgo> listarTodos() throws SQLException;

    /**
     * Lista el historial de predicciones de un estudiante, de la más reciente a la más antigua.
     *
     * @param idEstudiante id del estudiante
     * @return predicciones del estudiante
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<PrediccionRiesgo> listarPorEstudiante(int idEstudiante) throws SQLException;

    /**
     * Obtiene la predicción más reciente de un estudiante. La usan el panel y RiesgoResource.
     *
     * @param idEstudiante id del estudiante
     * @return la última predicción o null si aún no se ha calculado
     * @throws SQLException si ocurre un error en la base de datos
     */
    PrediccionRiesgo buscarUltimaPorEstudiante(int idEstudiante) throws SQLException;

    /**
     * Lista la predicción más reciente de cada estudiante con el nivel indicado.
     * Sirve para el consolidado de riesgo del administrador.
     *
     * @param nivelRiesgo Bajo, Medio o Alto
     * @return predicciones vigentes con ese nivel
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<PrediccionRiesgo> listarVigentesPorNivel(String nivelRiesgo) throws SQLException;
}