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
     * Actualiza un registro existente. Si el estado queda en "Activa", verifica que el estudiante
     * no tenga otra matrícula activa en el mismo año lectivo.
     *
     * @param matricula datos actualizados; debe tener su id
     * @throws SQLException si ya existe otra matrícula activa en ese año lectivo
     *                      o si ocurre un error en la base de datos
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
     * Regla de negocio: un estudiante solo puede tener una matrícula "Activa" por año lectivo.
     * Si ya la tiene, no se guarda nada y se lanza una excepción con el mensaje para el usuario.
     * Si la matrícula anterior está "Retirado" o "Trasladado", sí se permite matricular de nuevo.
     *
     * @param matricula datos a registrar
     * @throws SQLException si el estudiante ya tiene una matrícula activa en ese año lectivo
     *                      o si ocurre un error en la base de datos
     */
    void procesarMatricula(Matricula matricula) throws SQLException;

    /**
     * Indica si un estudiante ya tiene una matrícula "Activa" en un año lectivo.
     *
     * @param idEstudiante id del estudiante
     * @param anioLectivo  año lectivo (por ejemplo, "2026")
     * @return true si ya existe una matrícula activa
     * @throws SQLException si ocurre un error en la base de datos
     */
    boolean existeMatriculaActiva(int idEstudiante, String anioLectivo) throws SQLException;

    /**
     * Lista las matrículas de una sección.
     *
     * @param idSeccion valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Matricula> listarPorSeccion(int idSeccion) throws SQLException;
}
