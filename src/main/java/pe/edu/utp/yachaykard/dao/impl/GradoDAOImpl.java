package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.GradoDAO;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link GradoDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class GradoDAOImpl implements GradoDAO {

    @Override
    public void insertar(Grado grado) throws SQLException {
        String sql = "INSERT INTO grado (numero_grado, nivel) VALUES (?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, grado);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Grado grado) throws SQLException {
        String sql = "UPDATE grado SET numero_grado = ?, nivel = ? WHERE id_grado = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, grado);
            ps.setInt(3, grado.getIdGrado());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idGrado) throws SQLException {
        String sql = "DELETE FROM grado WHERE id_grado = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idGrado);
            ps.executeUpdate();
        }
    }

    @Override
    public Grado buscarPorId(int idGrado) throws SQLException {
        List<Grado> lista = consultar("SELECT * FROM grado WHERE id_grado = ?", idGrado);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Grado> listarTodos() throws SQLException {
        String sql = "SELECT * FROM grado";
        List<Grado> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearGrado(rs));
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
    private List<Grado> consultar(String sql, int id) throws SQLException {
        List<Grado> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearGrado(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param grado datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Grado grado) throws SQLException {
        ps.setString(1, grado.getNumeroGrado());
        ps.setString(2, grado.getNivel());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Grado.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Grado mapearGrado(ResultSet rs) throws SQLException {
        Grado grado = new Grado();
        grado.setIdGrado(rs.getInt("id_grado"));
        grado.setNumeroGrado(rs.getString("numero_grado"));
        grado.setNivel(rs.getString("nivel"));
        return grado;
    }
}
