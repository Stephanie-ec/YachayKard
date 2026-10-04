package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.SesionDAO;
import pe.edu.utp.yachaykard.modelo.Sesion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SesionDAOImpl implements SesionDAO {

    @Override
    public void insertar(Sesion sesion) throws SQLException {
        String sql = "INSERT INTO sesion (fecha_inicio, id_usuario) VALUES (?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaInicio()));
            ps.setInt(2, sesion.getIdUsuario());

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Sesion sesion) throws SQLException {
        String sql = "UPDATE sesion SET fecha_inicio = ?, id_usuario = ? WHERE id_sesion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaInicio()));
            ps.setInt(2, sesion.getIdUsuario());
            ps.setInt(3, sesion.getIdSesion());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idSesion) throws SQLException {
        String sql = "DELETE FROM sesion WHERE id_sesion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSesion);
            ps.executeUpdate();
        }
    }

    @Override
    public Sesion buscarPorId(int idSesion) throws SQLException {
        String sql = "SELECT * FROM sesion WHERE id_sesion = ?";
        Sesion sesion = null;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSesion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sesion = mapearSesion(rs);
                }
            }
        }
        return sesion;
    }

    @Override
    public List<Sesion> listarTodos() throws SQLException {
        String sql = "SELECT * FROM sesion";
        List<Sesion> sesiones = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sesiones.add(mapearSesion(rs));
            }
        }
        return sesiones;
    }

    private Sesion mapearSesion(ResultSet rs) throws SQLException {
        Sesion sesion = new Sesion();
        sesion.setIdSesion(rs.getInt("id_sesion"));
        sesion.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());
        sesion.setIdUsuario(rs.getInt("id_usuario"));
        return sesion;
    }
}