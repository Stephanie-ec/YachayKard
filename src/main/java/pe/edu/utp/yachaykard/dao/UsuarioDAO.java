package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones de acceso a datos de la tabla usuario.
 */
public interface UsuarioDAO {

    /**
     * Inserta un nuevo registro.
     *
     * @param usuario datos a guardar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void insertar(Usuario usuario) throws SQLException;

    /**
     * Actualiza un registro existente.
     *
     * @param usuario datos actualizados; debe tener su id
     * @throws SQLException si ocurre un error en la base de datos
     */
    void actualizar(Usuario usuario) throws SQLException;

    /**
     * Elimina un registro por su id.
     *
     * @param idUsuario id del registro
     * @throws SQLException si ocurre un error o el registro está en uso
     */
    void eliminar(int idUsuario) throws SQLException;

    /**
     * Busca un registro por su id.
     *
     * @param idUsuario id del registro
     * @return el registro encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Usuario buscarPorId(int idUsuario) throws SQLException;

    /**
     * Lista todos los registros.
     *
     * @return lista de registros (vacía si no hay ninguno)
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Usuario> listarTodos() throws SQLException;

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return el usuario encontrado o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    Usuario buscarPorUsername(String username) throws SQLException;

    /**
     * Comprueba si el usuario y la contraseña son correctos
     * (método autenticar() del diagrama de clases).
     *
     * @param username nombre de usuario
     * @param password contraseña ingresada
     * @return true si las credenciales coinciden
     * @throws SQLException si ocurre un error en la base de datos
     */
    boolean autenticar(String username, String password) throws SQLException;

    /**
     * Registra un usuario y devuelve el id que le asignó la base de datos.
     * La contraseña se guarda cifrada.
     *
     * @param usuario datos del usuario (password en texto plano)
     * @return id del usuario nuevo
     * @throws SQLException si ocurre un error en la base de datos
     */
    int registrar(Usuario usuario) throws SQLException;

    /**
     * Activa o desactiva un usuario. Un usuario inactivo no puede iniciar sesión.
     *
     * @param idUsuario usuario a cambiar
     * @param activo    true para activar, false para desactivar
     * @throws SQLException si ocurre un error en la base de datos
     */
    void cambiarEstado(int idUsuario, boolean activo) throws SQLException;
}