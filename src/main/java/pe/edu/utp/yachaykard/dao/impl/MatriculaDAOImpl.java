package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.MatriculaDAO;
import pe.edu.utp.yachaykard.modelo.Matricula;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link MatriculaDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class MatriculaDAOImpl implements MatriculaDAO {

    @Override
    public void insertar(Matricula matricula) throws SQLException {
        String sql = "INSERT INTO matricula (fecha_matricula, anio_lectivo, estado, id_estudiante, id_seccion) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, matricula);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Matricula matricula) throws SQLException {
        String sql = "UPDATE matricula SET fecha_matricula = ?, anio_lectivo = ?, estado = ?, id_estudiante = ?, id_seccion = ? WHERE id_matricula = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, matricula);
            ps.setInt(6, matricula.getIdMatricula());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idMatricula) throws SQLException {
        String sql = "DELETE FROM matricula WHERE id_matricula = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMatricula);
            ps.executeUpdate();
        }
    }

    @Override
    public Matricula buscarPorId(int idMatricula) throws SQLException {
        List<Matricula> lista = consultar("SELECT * FROM matricula WHERE id_matricula = ?", idMatricula);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Matricula> listarTodos() throws SQLException {
        String sql = "SELECT * FROM matricula";
        List<Matricula> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMatricula(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Matricula> listarPorSeccion(int idSeccion) throws SQLException {
        return consultar("SELECT * FROM matricula WHERE id_seccion = ?", idSeccion);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Matricula> consultar(String sql, int id) throws SQLException {
        List<Matricula> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMatricula(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param matricula datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Matricula matricula) throws SQLException {
        ps.setDate(1, matricula.getFechaMatricula() != null ? Date.valueOf(matricula.getFechaMatricula()) : null);
        ps.setString(2, matricula.getAnioLectivo());
        ps.setString(3, matricula.getEstado());
        ps.setInt(4, matricula.getIdEstudiante());
        ps.setInt(5, matricula.getIdSeccion());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Matricula.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Matricula mapearMatricula(ResultSet rs) throws SQLException {
        Matricula matricula = new Matricula();
        matricula.setIdMatricula(rs.getInt("id_matricula"));
        matricula.setFechaMatricula(rs.getDate("fecha_matricula") != null ? rs.getDate("fecha_matricula").toLocalDate() : null);
        matricula.setAnioLectivo(rs.getString("anio_lectivo"));
        matricula.setEstado(rs.getString("estado"));
        matricula.setIdEstudiante(rs.getInt("id_estudiante"));
        matricula.setIdSeccion(rs.getInt("id_seccion"));
        return matricula;
    }
}
