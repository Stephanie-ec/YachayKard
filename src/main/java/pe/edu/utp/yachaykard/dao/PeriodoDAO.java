package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Periodo;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla periodo.
 */
public interface PeriodoDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param periodo datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Periodo periodo) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param periodo datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Periodo periodo) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idPeriodo id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idPeriodo) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idPeriodo id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Periodo buscarPorId(int idPeriodo) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Periodo> listarTodos() throws SQLException;

    /**
     * Abre un periodo para que se puedan registrar calificaciones
     * (método abrirPeriodo() del diagrama de clases).
     *
     * @param idPeriodo periodo a abrir
     * @throws SQLException si ocurre un error en la base de datos
     */
    void abrirPeriodo(int idPeriodo) throws SQLException;

    /**
     * Cierra un periodo; ya no se podrán editar sus calificaciones
     * (método cerrarPeriodo() del diagrama de clases).
     *
     * @param idPeriodo periodo a cerrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void cerrarPeriodo(int idPeriodo) throws SQLException;
}