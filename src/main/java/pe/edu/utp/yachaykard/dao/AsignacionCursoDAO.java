package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.AsignacionCurso;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla asignacion_curso.
 */
public interface AsignacionCursoDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param asignacionCurso datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(AsignacionCurso asignacionCurso) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param asignacionCurso datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(AsignacionCurso asignacionCurso) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idAsignacion id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idAsignacion) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idAsignacion id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    AsignacionCurso buscarPorId(int idAsignacion) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<AsignacionCurso> listarTodos() throws SQLException;

    /**
     * Asigna un docente a un curso y sección (método asignarDocente() del diagrama de clases).
     *
     * @param asignacionCurso datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void asignarDocente(AsignacionCurso asignacionCurso) throws SQLException {
        insertar(asignacionCurso);
    }

    /**
     * Lista los cursos y secciones asignados a un docente (pantalla Mis secciones).
     *
     * @param idDocente valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<AsignacionCurso> listarPorDocente(int idDocente) throws SQLException;
}