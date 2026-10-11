package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.RolPermisoDAO;
import pe.edu.utp.yachaykard.modelo.RolPermiso;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link RolPermisoDAO}.
 */
public class RolPermisoDAOImpl implements RolPermisoDAO {

    @Override
    public void insertar(RolPermiso rolPermiso) throws SQLException {
        String sql = "INSERT INTO rol_permiso (id_rol, id_permiso) VALUES (?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rolPermiso.getIdRol());
            ps.setInt(2, rolPermiso.getIdPermiso());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idRol, int idPermiso) throws SQLException {
        String sql = "DELETE FROM rol_permiso WHERE id_rol = ? AND id_permiso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ps.setInt(2, idPermiso);
            ps.executeUpdate();
        }
    }

    @Override
    public List<RolPermiso> listarTodos() throws SQLException {
        String sql = "SELECT * FROM rol_permiso";
        List<RolPermiso> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearRolPermiso(rs));
            }
        }
        return lista;
    }

    @Override
    public List<RolPermiso> listarPorRol(int idRol) throws SQLException {
        String sql = "SELECT * FROM rol_permiso WHERE id_rol = ?";
        List<RolPermiso> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRolPermiso(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean existe(int idRol, int idPermiso) throws SQLException {
        String sql = "SELECT 1 FROM rol_permiso WHERE id_rol = ? AND id_permiso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ps.setInt(2, idPermiso);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto RolPermiso.
     *
     * @param rs resultado posicionado en una fila
     * @return relación con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private RolPermiso mapearRolPermiso(ResultSet rs) throws SQLException {
        return new RolPermiso(rs.getInt("id_rol"), rs.getInt("id_permiso"));
    }
}