package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla seccion.
 */
public interface SeccionDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param seccion datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Seccion seccion) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param seccion datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Seccion seccion) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idSeccion id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idSeccion) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idSeccion id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Seccion buscarPorId(int idSeccion) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Seccion> listarTodos() throws SQLException;

    /**
     * Crea una sección (método crearSeccion() del diagrama de clases).
     *
     * @param seccion datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void crearSeccion(Seccion seccion) throws SQLException {
        insertar(seccion);
    }

    /**
     * Lista las secciones de las que un docente es tutor.
     *
     * @param idDocenteTutor valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Seccion> listarPorTutor(int idDocenteTutor) throws SQLException;
}
