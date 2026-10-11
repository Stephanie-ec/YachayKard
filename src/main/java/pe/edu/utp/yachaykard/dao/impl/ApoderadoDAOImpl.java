package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.ApoderadoDAO;
import pe.edu.utp.yachaykard.modelo.Apoderado;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link ApoderadoDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class ApoderadoDAOImpl implements ApoderadoDAO {

    @Override
    public void insertar(Apoderado apoderado) throws SQLException {
        String sql = "INSERT INTO apoderado (dni, nombres, telefono) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, apoderado);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Apoderado apoderado) throws SQLException {
        String sql = "UPDATE apoderado SET dni = ?, nombres = ?, telefono = ? WHERE id_apoderado = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, apoderado);
            ps.setInt(4, apoderado.getIdApoderado());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idApoderado) throws SQLException {
        String sql = "DELETE FROM apoderado WHERE id_apoderado = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idApoderado);
            ps.executeUpdate();
        }
    }

    @Override
    public Apoderado buscarPorId(int idApoderado) throws SQLException {
        List<Apoderado> lista = consultar("SELECT * FROM apoderado WHERE id_apoderado = ?", idApoderado);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Apoderado> listarTodos() throws SQLException {
        String sql = "SELECT * FROM apoderado";
        List<Apoderado> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearApoderado(rs));
            }
        }
        return lista;
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Apoderado> consultar(String sql, int id) throws SQLException {
        List<Apoderado> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearApoderado(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param apoderado datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Apoderado apoderado) throws SQLException {
        ps.setString(1, apoderado.getDni());
        ps.setString(2, apoderado.getNombres());
        ps.setString(3, apoderado.getTelefono());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Apoderado.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Apoderado mapearApoderado(ResultSet rs) throws SQLException {
        Apoderado apoderado = new Apoderado();
        apoderado.setIdApoderado(rs.getInt("id_apoderado"));
        apoderado.setDni(rs.getString("dni"));
        apoderado.setNombres(rs.getString("nombres"));
        apoderado.setTelefono(rs.getString("telefono"));
        return apoderado;
    }
}
