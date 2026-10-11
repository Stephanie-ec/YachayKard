package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Curso;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla curso.
 */
public interface CursoDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param curso datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Curso curso) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param curso datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Curso curso) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idCurso id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idCurso) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idCurso id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Curso buscarPorId(int idCurso) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Curso> listarTodos() throws SQLException;

    /**
     * Registra un curso (método registrarCurso() del diagrama de clases).
     *
     * @param curso datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarCurso(Curso curso) throws SQLException {
        insertar(curso);
    }
}
