package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Asistencia;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla asistencia.
 */
public interface AsistenciaDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param asistencia datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Asistencia asistencia) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param asistencia datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Asistencia asistencia) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idAsistencia id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idAsistencia) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idAsistencia id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Asistencia buscarPorId(int idAsistencia) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Asistencia> listarTodos() throws SQLException;

    /**
     * Registra la asistencia de un estudiante (método tomarAsistencia() del diagrama de clases).
     *
     * @param asistencia datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void tomarAsistencia(Asistencia asistencia) throws SQLException {
        insertar(asistencia);
    }

    /**
     * Lista la asistencia de un estudiante (se usa en el historial y en la predicción de riesgo).
     *
     * @param idEstudiante valor a filtrar
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Asistencia> listarPorEstudiante(int idEstudiante) throws SQLException;

    /**
     * Lista la asistencia de un mes de los estudiantes con matrícula activa en una sección
     * (base del consolidado mensual, RF-21).
     *
     * @param idSeccion sección a consultar
     * @param anio      año (por ejemplo 2026)
     * @param mes       mes de 1 a 12
     * @return registros del mes, ordenados por estudiante y fecha
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Asistencia> listarPorSeccionYMes(int idSeccion, int anio, int mes) throws SQLException;
}
