package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link CursoDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class CursoDAOImpl implements CursoDAO {

    @Override
    public void insertar(Curso curso) throws SQLException {
        String sql = "INSERT INTO curso (nombre_curso, horas_semanales, codigo_siagie) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, curso);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Curso curso) throws SQLException {
        String sql = "UPDATE curso SET nombre_curso = ?, horas_semanales = ?, codigo_siagie = ? WHERE id_curso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, curso);
            ps.setInt(4, curso.getIdCurso());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idCurso) throws SQLException {
        String sql = "DELETE FROM curso WHERE id_curso = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            ps.executeUpdate();
        }
    }

    @Override
    public Curso buscarPorId(int idCurso) throws SQLException {
        List<Curso> lista = consultar("SELECT * FROM curso WHERE id_curso = ?", idCurso);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Curso> listarTodos() throws SQLException {
        String sql = "SELECT * FROM curso";
        List<Curso> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCurso(rs));
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
    private List<Curso> consultar(String sql, int id) throws SQLException {
        List<Curso> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCurso(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param curso datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Curso curso) throws SQLException {
        ps.setString(1, curso.getNombreCurso());
        ps.setInt(2, curso.getHorasSemanales());
        ps.setString(3, curso.getCodigoSiagie());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Curso.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Curso mapearCurso(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setIdCurso(rs.getInt("id_curso"));
        curso.setNombreCurso(rs.getString("nombre_curso"));
        curso.setHorasSemanales(rs.getInt("horas_semanales"));
        curso.setCodigoSiagie(rs.getString("codigo_siagie"));
        return curso;
    }
}
