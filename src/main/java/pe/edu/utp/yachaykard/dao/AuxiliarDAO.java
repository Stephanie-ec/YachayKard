package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Auxiliar;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla auxiliar.
 */
public interface AuxiliarDAO {

    /**
     * Inserta un nuevo auxiliar.
     *
     * @param auxiliar datos a guardar
     * @throws SQLException si ocurre un error o el usuario ya está vinculado a otro auxiliar
     */
    void insertar(Auxiliar auxiliar) throws SQLException;

    /**
     * Actualiza los datos de un auxiliar existente.
     *
     * @param auxiliar datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Auxiliar auxiliar) throws SQLException;

    /**
     * Elimina un auxiliar por su id.
     *
     * @param idAuxiliar id del auxiliar
     * @throws SQLException si ocurre un error o tiene asistencias registradas
     */
    void eliminar(int idAuxiliar) throws SQLException;

    /**
     * Busca un auxiliar por su id.
     *
     * @param idAuxiliar id del auxiliar
     * @return el auxiliar encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Auxiliar buscarPorId(int idAuxiliar) throws SQLException;

    /**
     * Lista todos los auxiliares registrados.
     *
     * @return lista de auxiliares (vacía si no hay registros)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Auxiliar> listarTodos() throws SQLException;

    /**
     * Busca el auxiliar vinculado a un usuario. Se usa al iniciar sesión.
     *
     * @param idUsuario id del usuario
     * @return el auxiliar vinculado o null si el usuario no es auxiliar
     * @throws SQLException si ocurre un error en la base de datos
     */
    Auxiliar buscarPorUsuario(int idUsuario) throws SQLException;

    /**
     * Registra un auxiliar (método registrarAuxiliar() del diagrama de clases).
     *
     * @param auxiliar datos a registrar
     * @throws SQLException si ocurre un error en la base de datos
     */
    default void registrarAuxiliar(Auxiliar auxiliar) throws SQLException {
        insertar(auxiliar);
    }
}