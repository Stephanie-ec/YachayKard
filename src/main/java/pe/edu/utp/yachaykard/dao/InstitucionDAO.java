package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Institucion;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla institucion.
 */
public interface InstitucionDAO {

    /**
     * Inserta una nueva institución.
     *
     * @param institucion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Institucion institucion) throws SQLException;

    /**
     * Actualiza los datos de una institución existente.
     *
     * @param institucion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Institucion institucion) throws SQLException;

    /**
     * Elimina una institución por su id.
     *
     * @param idInstitucion id de la institución
     * @throws SQLException si ocurre un error o tiene usuarios asociados
     */
    void eliminar(int idInstitucion) throws SQLException;

    /**
     * Busca una institución por su id.
     *
     * @param idInstitucion id de la institución
     * @return la institución encontrada o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Institucion buscarPorId(int idInstitucion) throws SQLException;

    /**
     * Lista todas las instituciones registradas.
     *
     * @return lista de instituciones (vacía si no hay registros)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Institucion> listarTodos() throws SQLException;

    /**
     * Registra los datos de la institución (método registrar() del diagrama de clases).
     *
     * @param institucion datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrar(Institucion institucion) throws SQLException {
        insertar(institucion);
    }
}