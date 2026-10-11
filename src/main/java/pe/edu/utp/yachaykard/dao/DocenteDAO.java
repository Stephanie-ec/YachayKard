package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Docente;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla docente.
 */
public interface DocenteDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param docente datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Docente docente) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param docente datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Docente docente) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idDocente id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idDocente) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idDocente id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Docente buscarPorId(int idDocente) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Docente> listarTodos() throws SQLException;

    /**
     * Registra un docente (método registrarDocente() del diagrama de clases).
     *
     * @param docente datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarDocente(Docente docente) throws SQLException {
        insertar(docente);
    }

    /**
     * Busca el docente vinculado a un usuario (se usa al iniciar sesión).
     *
     * @param idUsuario valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Docente> listarPorUsuario(int idUsuario) throws SQLException;
}
