package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Competencia;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla competencia.
 */
public interface CompetenciaDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param competencia datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Competencia competencia) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param competencia datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Competencia competencia) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idCompetencia id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idCompetencia) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idCompetencia id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Competencia buscarPorId(int idCompetencia) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Competencia> listarTodos() throws SQLException;

    /**
     * Registra una competencia (método registrarCompetencia() del diagrama de clases).
     *
     * @param competencia datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarCompetencia(Competencia competencia) throws SQLException {
        insertar(competencia);
    }

    /**
     * Lista las competencias de un curso o área.
     *
     * @param idCurso valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Competencia> listarPorCurso(int idCurso) throws SQLException;
}