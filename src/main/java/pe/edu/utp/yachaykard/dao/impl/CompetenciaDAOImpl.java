package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.CompetenciaDAO;
import pe.edu.utp.yachaykard.modelo.Competencia;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link CompetenciaDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class CompetenciaDAOImpl implements CompetenciaDAO {

    @Override
    public void insertar(Competencia competencia) throws SQLException {
        String sql = "INSERT INTO competencia (descripcion, id_curso, codigo) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, competencia);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Competencia competencia) throws SQLException {
        String sql = "UPDATE competencia SET descripcion = ?, id_curso = ?, codigo = ? WHERE id_competencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, competencia);
            ps.setInt(4, competencia.getIdCompetencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idCompetencia) throws SQLException {
        String sql = "DELETE FROM competencia WHERE id_competencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCompetencia);
            ps.executeUpdate();
        }
    }

    @Override
    public Competencia buscarPorId(int idCompetencia) throws SQLException {
        List<Competencia> lista = consultar("SELECT * FROM competencia WHERE id_competencia = ?", idCompetencia);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Competencia> listarTodos() throws SQLException {
        String sql = "SELECT * FROM competencia";
        List<Competencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCompetencia(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Competencia> listarPorCurso(int idCurso) throws SQLException {
        return consultar("SELECT * FROM competencia WHERE id_curso = ? ORDER BY codigo", idCurso);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Competencia> consultar(String sql, int id) throws SQLException {
        List<Competencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCompetencia(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps          sentencia preparada
     * @param competencia datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Competencia competencia) throws SQLException {
        ps.setString(1, competencia.getDescripcion());
        ps.setInt(2, competencia.getIdCurso());
        ps.setString(3, competencia.getCodigo());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Competencia.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Competencia mapearCompetencia(ResultSet rs) throws SQLException {
        Competencia competencia = new Competencia();
        competencia.setIdCompetencia(rs.getInt("id_competencia"));
        competencia.setDescripcion(rs.getString("descripcion"));
        competencia.setIdCurso(rs.getInt("id_curso"));
        competencia.setCodigo(rs.getString("codigo"));
        return competencia;
    }
}