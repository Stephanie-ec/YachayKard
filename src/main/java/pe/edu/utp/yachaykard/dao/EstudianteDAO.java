package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla estudiante.
 */
public interface EstudianteDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param estudiante datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Estudiante estudiante) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param estudiante datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Estudiante estudiante) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idEstudiante id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idEstudiante) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idEstudiante id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Estudiante buscarPorId(int idEstudiante) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Estudiante> listarTodos() throws SQLException;

    /**
     * Registra un estudiante (método registrar() del diagrama de clases).
     *
     * @param estudiante datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrar(Estudiante estudiante) throws SQLException {
        insertar(estudiante);
    }

    /**
     * Lista los estudiantes matriculados en una sección, ordenados por apellidos.
     *
     * @param idSeccion valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Estudiante> listarPorSeccion(int idSeccion) throws SQLException;
}
