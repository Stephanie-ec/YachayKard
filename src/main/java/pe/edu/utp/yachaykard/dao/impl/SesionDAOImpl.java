package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.SesionDAO;
import pe.edu.utp.yachaykard.modelo.Sesion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link SesionDAO}.
 * Convierte LocalDateTime a DATETIME con Timestamp.valueOf() y de vuelta con toLocalDateTime().
 */
public class SesionDAOImpl implements SesionDAO {

    @Override
    public void insertar(Sesion sesion) throws SQLException {
        String sql = "INSERT INTO sesion (fecha_inicio, fecha_fin, id_usuario) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaInicio()));
            asignarFechaFin(ps, 2, sesion.getFechaFin());
            ps.setInt(3, sesion.getIdUsuario());

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Sesion sesion) throws SQLException {
        String sql = "UPDATE sesion SET fecha_inicio = ?, fecha_fin = ?, id_usuario = ? WHERE id_sesion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(sesion.getFechaInicio()));
            asignarFechaFin(ps, 2, sesion.getFechaFin());
            ps.setInt(3, sesion.getIdUsuario());
            ps.setInt(4, sesion.getIdSesion());

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

    @Override
    public int iniciarSesion(int idUsuario) throws SQLException {
        String sql = "INSERT INTO sesion (fecha_inicio, id_usuario) VALUES (?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idUsuario);
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id de la nueva sesión.");
    }

    @Override
    public void cerrarSesion(int idSesion) throws SQLException {
        String sql = "UPDATE sesion SET fecha_fin = ? WHERE id_sesion = ? AND fecha_fin IS NULL";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idSesion);
            ps.executeUpdate();
        }
    }

    /**
     * Asigna fecha_fin, que puede ser null mientras la sesión esté abierta.
     *
     * @param ps       sentencia preparada
     * @param indice   posición del parámetro
     * @param fechaFin fecha de cierre o null
     * @throws SQLException si falla la asignación
     */
    private void asignarFechaFin(PreparedStatement ps, int indice, LocalDateTime fechaFin) throws SQLException {
        if (fechaFin == null) {
            ps.setNull(indice, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(indice, Timestamp.valueOf(fechaFin));
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Sesion.
     *
     * @param rs resultado posicionado en una fila
     * @return sesión con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Sesion mapearSesion(ResultSet rs) throws SQLException {
        Sesion sesion = new Sesion();
        sesion.setIdSesion(rs.getInt("id_sesion"));
        sesion.setFechaInicio(rs.getTimestamp("fecha_inicio").toLocalDateTime());
        Timestamp fin = rs.getTimestamp("fecha_fin");
        sesion.setFechaFin(fin != null ? fin.toLocalDateTime() : null);
        sesion.setIdUsuario(rs.getInt("id_usuario"));
        return sesion;
    }
}