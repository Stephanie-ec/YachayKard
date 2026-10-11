package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Matricula;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla matricula.
 */
public interface MatriculaDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param matricula datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Matricula matricula) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param matricula datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Matricula matricula) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idMatricula id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idMatricula) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idMatricula id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Matricula buscarPorId(int idMatricula) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Matricula> listarTodos() throws SQLException;

    /**
     * Procesa la matrícula de un estudiante (método procesarMatricula() del diagrama de clases).
     *
     * @param matricula datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void procesarMatricula(Matricula matricula) throws SQLException {
        insertar(matricula);
    }

    /**
     * Lista las matrículas de una sección.
     *
     * @param idSeccion valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Matricula> listarPorSeccion(int idSeccion) throws SQLException;
}
