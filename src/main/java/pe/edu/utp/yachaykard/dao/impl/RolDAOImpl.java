package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.RolDAO;
import pe.edu.utp.yachaykard.modelo.Rol;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAOImpl implements RolDAO {

    @Override
    public void insertar(Rol rol) throws SQLException {
        String sql = "INSERT INTO rol (nombre_rol) VALUES (?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rol.getNombreRol());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Rol rol) throws SQLException {
        String sql = "UPDATE rol SET nombre_rol = ? WHERE id_rol = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rol.getNombreRol());
            ps.setInt(2, rol.getIdRol());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idRol) throws SQLException {
        String sql = "DELETE FROM rol WHERE id_rol = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ps.executeUpdate();
        }
    }

    @Override
    public Rol buscarPorId(int idRol) throws SQLException {
        String sql = "SELECT * FROM rol WHERE id_rol = ?";
        Rol rol = null;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rol = mapearRol(rs);
                }
            }
        }
        return rol;
    }

    @Override
    public List<Rol> listarTodos() throws SQLException {
        String sql = "SELECT * FROM rol";
        List<Rol> roles = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(mapearRol(rs));
            }
        }
        return roles;
    }

    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombreRol(rs.getString("nombre_rol"));
        return rol;
    }
}