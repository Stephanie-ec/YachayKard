package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.CalificacionDAO;
import pe.edu.utp.yachaykard.modelo.Calificacion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link CalificacionDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class CalificacionDAOImpl implements CalificacionDAO {

    @Override
    public void insertar(Calificacion calificacion) throws SQLException {
        String sql = "INSERT INTO calificacion (id_estudiante, id_competencia, id_periodo, observacion, mes, nivel_mensual, fortaleza, debilidad, conclusion_descriptiva) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, calificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Calificacion calificacion) throws SQLException {
        String sql = "UPDATE calificacion SET id_estudiante = ?, id_competencia = ?, id_periodo = ?, observacion = ?, mes = ?, nivel_mensual = ?, fortaleza = ?, debilidad = ?, conclusion_descriptiva = ? WHERE id_calificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, calificacion);
            ps.setInt(10, calificacion.getIdCalificacion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idCalificacion) throws SQLException {
        String sql = "DELETE FROM calificacion WHERE id_calificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCalificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public Calificacion buscarPorId(int idCalificacion) throws SQLException {
        List<Calificacion> lista = consultar("SELECT * FROM calificacion WHERE id_calificacion = ?", idCalificacion);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Calificacion> listarTodos() throws SQLException {
        String sql = "SELECT * FROM calificacion";
        List<Calificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCalificacion(rs));
            }
        }
        return lista;
    }

    @Override
    public void registrarNota(Calificacion calificacion) throws SQLException {
        String sql = "INSERT INTO calificacion (id_estudiante, id_competencia, id_periodo, observacion, mes, "
                + "nivel_mensual, fortaleza, debilidad, conclusion_descriptiva) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) AS nueva "
                + "ON DUPLICATE KEY UPDATE observacion = nueva.observacion, nivel_mensual = nueva.nivel_mensual, "
                + "fortaleza = nueva.fortaleza, debilidad = nueva.debilidad, "
                + "conclusion_descriptiva = nueva.conclusion_descriptiva";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, calificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Calificacion> listarPorSeccion(int idSeccion, int idCompetencia, int idPeriodo, int mes)
            throws SQLException {
        String sql = "SELECT c.* FROM calificacion c "
                + "JOIN matricula m ON m.id_estudiante = c.id_estudiante "
                + "WHERE m.id_seccion = ? AND m.estado = 'Activa' "
                + "AND c.id_competencia = ? AND c.id_periodo = ? AND c.mes = ?";
        List<Calificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);
            ps.setInt(2, idCompetencia);
            ps.setInt(3, idPeriodo);
            ps.setInt(4, mes);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCalificacion(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Calificacion> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar("SELECT * FROM calificacion WHERE id_estudiante = ? ORDER BY id_periodo, mes", idEstudiante);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Calificacion> consultar(String sql, int id) throws SQLException {
        List<Calificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCalificacion(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param calificacion datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Calificacion calificacion) throws SQLException {
        ps.setInt(1, calificacion.getIdEstudiante());
        ps.setInt(2, calificacion.getIdCompetencia());
        ps.setInt(3, calificacion.getIdPeriodo());
        ps.setString(4, calificacion.getObservacion());
        ps.setInt(5, calificacion.getMes());
        ps.setString(6, calificacion.getNivelMensual());
        ps.setString(7, calificacion.getFortaleza());
        ps.setString(8, calificacion.getDebilidad());
        ps.setString(9, calificacion.getConclusionDescriptiva());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Calificacion.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Calificacion mapearCalificacion(ResultSet rs) throws SQLException {
        Calificacion calificacion = new Calificacion();
        calificacion.setIdCalificacion(rs.getInt("id_calificacion"));
        calificacion.setIdEstudiante(rs.getInt("id_estudiante"));
        calificacion.setIdCompetencia(rs.getInt("id_competencia"));
        calificacion.setIdPeriodo(rs.getInt("id_periodo"));
        calificacion.setObservacion(rs.getString("observacion"));
        calificacion.setMes(rs.getInt("mes"));
        calificacion.setNivelMensual(rs.getString("nivel_mensual"));
        calificacion.setFortaleza(rs.getString("fortaleza"));
        calificacion.setDebilidad(rs.getString("debilidad"));
        calificacion.setConclusionDescriptiva(rs.getString("conclusion_descriptiva"));
        return calificacion;
    }
}