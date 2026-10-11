package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.DocenteDAO;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link DocenteDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.*/
public class DocenteDAOImpl implements DocenteDAO {

    @Override
    public void insertar(Docente docente) throws SQLException {
        String sql = "INSERT INTO docente (dni, nombres, apellidos, especialidad, id_usuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, docente);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Docente docente) throws SQLException {
        String sql = "UPDATE docente SET dni = ?, nombres = ?, apellidos = ?, especialidad = ?, id_usuario = ? WHERE id_docente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, docente);
            ps.setInt(6, docente.getIdDocente());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idDocente) throws SQLException {
        String sql = "DELETE FROM docente WHERE id_docente = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDocente);
            ps.executeUpdate();
        }
    }

    @Override
    public Docente buscarPorId(int idDocente) throws SQLException {
        List<Docente> lista = consultar("SELECT * FROM docente WHERE id_docente = ?", idDocente);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Docente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM docente";
        List<Docente> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDocente(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Docente> listarPorUsuario(int idUsuario) throws SQLException {
        return consultar("SELECT * FROM docente WHERE id_usuario = ?", idUsuario);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Docente> consultar(String sql, int id) throws SQLException {
        List<Docente> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDocente(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps      sentencia preparada
     * @param docente datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Docente docente) throws SQLException {
        ps.setString(1, docente.getDni());
        ps.setString(2, docente.getNombres());
        ps.setString(3, docente.getApellidos());
        ps.setString(4, docente.getEspecialidad());
        ps.setInt(5, docente.getIdUsuario());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Docente.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Docente mapearDocente(ResultSet rs) throws SQLException {
        Docente docente = new Docente();
        docente.setIdDocente(rs.getInt("id_docente"));
        docente.setDni(rs.getString("dni"));
        docente.setNombres(rs.getString("nombres"));
        docente.setApellidos(rs.getString("apellidos"));
        docente.setEspecialidad(rs.getString("especialidad"));
        docente.setIdUsuario(rs.getInt("id_usuario"));
        return docente;
    }
}