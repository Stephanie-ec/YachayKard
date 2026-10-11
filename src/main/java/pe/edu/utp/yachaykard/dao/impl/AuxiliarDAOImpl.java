package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.AuxiliarDAO;
import pe.edu.utp.yachaykard.modelo.Auxiliar;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link AuxiliarDAO}.
 */
public class AuxiliarDAOImpl implements AuxiliarDAO {

    @Override
    public void insertar(Auxiliar auxiliar) throws SQLException {
        String sql = "INSERT INTO auxiliar (dni, nombres, apellidos, id_usuario) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auxiliar.getDni());
            ps.setString(2, auxiliar.getNombres());
            ps.setString(3, auxiliar.getApellidos());
            ps.setInt(4, auxiliar.getIdUsuario());

            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Auxiliar auxiliar) throws SQLException {
        String sql = "UPDATE auxiliar SET dni = ?, nombres = ?, apellidos = ?, id_usuario = ? WHERE id_auxiliar = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, auxiliar.getDni());
            ps.setString(2, auxiliar.getNombres());
            ps.setString(3, auxiliar.getApellidos());
            ps.setInt(4, auxiliar.getIdUsuario());
            ps.setInt(5, auxiliar.getIdAuxiliar());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idAuxiliar) throws SQLException {
        String sql = "DELETE FROM auxiliar WHERE id_auxiliar = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAuxiliar);
            ps.executeUpdate();
        }
    }

    @Override
    public Auxiliar buscarPorId(int idAuxiliar) throws SQLException {
        return buscarUno("SELECT * FROM auxiliar WHERE id_auxiliar = ?", idAuxiliar);
    }

    @Override
    public Auxiliar buscarPorUsuario(int idUsuario) throws SQLException {
        return buscarUno("SELECT * FROM auxiliar WHERE id_usuario = ?", idUsuario);
    }

    @Override
    public List<Auxiliar> listarTodos() throws SQLException {
        String sql = "SELECT * FROM auxiliar ORDER BY apellidos, nombres";
        List<Auxiliar> auxiliares = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                auxiliares.add(mapearAuxiliar(rs));
            }
        }
        return auxiliares;
    }

    /**
     * Ejecuta una consulta que filtra por un solo id y devuelve como máximo un auxiliar.
     *
     * @param sql consulta con un único parámetro
     * @param id  valor del parámetro
     * @return el auxiliar encontrado o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    private Auxiliar buscarUno(String sql, int id) throws SQLException {
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearAuxiliar(rs) : null;
            }
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Auxiliar.
     *
     * @param rs resultado posicionado en una fila
     * @return auxiliar con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Auxiliar mapearAuxiliar(ResultSet rs) throws SQLException {
        Auxiliar auxiliar = new Auxiliar();
        auxiliar.setIdAuxiliar(rs.getInt("id_auxiliar"));
        auxiliar.setDni(rs.getString("dni"));
        auxiliar.setNombres(rs.getString("nombres"));
        auxiliar.setApellidos(rs.getString("apellidos"));
        auxiliar.setIdUsuario(rs.getInt("id_usuario"));
        return auxiliar;
    }
}