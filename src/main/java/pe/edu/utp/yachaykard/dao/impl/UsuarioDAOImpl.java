package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.UsuarioDAO;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.CifradoClave;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link UsuarioDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    /** Columnas que se leen de la tabla usuario. */
    private static final String COLUMNAS =
            "id_usuario, username, password, id_rol, id_institucion, activo";

    /** Inicio común de las consultas de esta tabla. */
    private static final String CONSULTA_BASE = "SELECT " + COLUMNAS + " FROM usuario";

    @Override
    public void insertar(Usuario usuario) throws SQLException {
        registrar(usuario);
    }

    @Override
    public int registrar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (username, password, id_rol, id_institucion, activo) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, CifradoClave.cifrar(usuario.getPassword()));
            ps.setInt(3, usuario.getIdRol());
            ps.setInt(4, usuario.getIdInstitucion());
            ps.setBoolean(5, usuario.isActivo());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("No se obtuvo el id del usuario nuevo.");
                }
                int id = claves.getInt(1);
                usuario.setIdUsuario(id);
                return id;
            }
        }
    }

    /**
     * Actualiza los datos del usuario. Si password viene vacío, se conserva la contraseña actual.
     */
    @Override
    public void actualizar(Usuario usuario) throws SQLException {
        boolean cambiaClave = usuario.getPassword() != null && !usuario.getPassword().isBlank();
        String sql = cambiaClave
                ? "UPDATE usuario SET username = ?, id_rol = ?, id_institucion = ?, password = ? WHERE id_usuario = ?"
                : "UPDATE usuario SET username = ?, id_rol = ?, id_institucion = ? WHERE id_usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setInt(2, usuario.getIdRol());
            ps.setInt(3, usuario.getIdInstitucion());
            if (cambiaClave) {
                ps.setString(4, CifradoClave.cifrar(usuario.getPassword()));
                ps.setInt(5, usuario.getIdUsuario());
            } else {
                ps.setInt(4, usuario.getIdUsuario());
            }
            ps.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int idUsuario, boolean activo) throws SQLException {
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public Usuario buscarPorId(int idUsuario) throws SQLException {
        String sql = CONSULTA_BASE + " WHERE id_usuario = ?";
        Usuario usuario = null;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        }
        return usuario;
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = CONSULTA_BASE + " ORDER BY username";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = CONSULTA_BASE + " WHERE username = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearUsuario(rs) : null;
            }
        }
    }

    @Override
    public boolean autenticar(String username, String password) throws SQLException {
        String sql = "SELECT password FROM usuario WHERE username = ? AND activo = 1";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && CifradoClave.verificar(password, rs.getString("password"));
            }
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Usuario.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setIdInstitucion(rs.getInt("id_institucion"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }
}