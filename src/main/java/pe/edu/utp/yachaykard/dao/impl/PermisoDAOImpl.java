package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.PermisoDAO;
import pe.edu.utp.yachaykard.modelo.Permiso;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link PermisoDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class PermisoDAOImpl implements PermisoDAO {

    @Override
    public void insertar(Permiso permiso) throws SQLException {
        String sql = "INSERT INTO permiso (nombre_permiso) VALUES (?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, permiso.getNombrePermiso());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Permiso permiso) throws SQLException {
        String sql = "UPDATE permiso SET nombre_permiso = ? WHERE id_permiso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, permiso.getNombrePermiso());
            ps.setInt(2, permiso.getIdPermiso());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idPermiso) throws SQLException {
        String sql = "DELETE FROM permiso WHERE id_permiso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPermiso);
            ps.executeUpdate();
        }
    }

    @Override
    public Permiso buscarPorId(int idPermiso) throws SQLException {
        String sql = "SELECT * FROM permiso WHERE id_permiso = ?";
        Permiso permiso = null;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPermiso);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    permiso = mapearPermiso(rs);
                }
            }
        }
        return permiso;
    }

    @Override
    public List<Permiso> listarTodos() throws SQLException {
        String sql = "SELECT * FROM permiso";
        List<Permiso> permisos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                permisos.add(mapearPermiso(rs));
            }
        }
        return permisos;
    }

    @Override
    public boolean verificar(int idRol, String nombrePermiso) throws SQLException {
        String sql = "SELECT 1 FROM rol_permiso rp " +
                "JOIN permiso p ON p.id_permiso = rp.id_permiso " +
                "WHERE rp.id_rol = ? AND p.nombre_permiso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ps.setString(2, nombrePermiso);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Permiso.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Permiso mapearPermiso(ResultSet rs) throws SQLException {
        Permiso permiso = new Permiso();
        permiso.setIdPermiso(rs.getInt("id_permiso"));
        permiso.setNombrePermiso(rs.getString("nombre_permiso"));
        return permiso;
    }
}