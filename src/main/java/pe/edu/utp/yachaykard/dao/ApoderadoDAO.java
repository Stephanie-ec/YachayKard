package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Apoderado;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla apoderado.
 */
public interface ApoderadoDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param apoderado datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Apoderado apoderado) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param apoderado datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Apoderado apoderado) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idApoderado id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idApoderado) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idApoderado id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Apoderado buscarPorId(int idApoderado) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Apoderado> listarTodos() throws SQLException;

    /**
     * Registra un apoderado (método registrar() del diagrama de clases).
     *
     * @param apoderado datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrar(Apoderado apoderado) throws SQLException {
        insertar(apoderado);
    }
}
