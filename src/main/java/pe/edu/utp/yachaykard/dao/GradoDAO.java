package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Grado;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla grado.
 */
public interface GradoDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param grado datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Grado grado) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param grado datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Grado grado) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idGrado id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idGrado) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idGrado id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Grado buscarPorId(int idGrado) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Grado> listarTodos() throws SQLException;

    /**
     * Crea un grado (método crear() del diagrama de clases).
     *
     * @param grado datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void crear(Grado grado) throws SQLException {
        insertar(grado);
    }
}
