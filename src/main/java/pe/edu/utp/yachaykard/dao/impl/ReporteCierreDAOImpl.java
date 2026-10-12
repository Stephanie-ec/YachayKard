package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.modelo.ReporteCierre;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link ReporteCierreDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class ReporteCierreDAOImpl implements ReporteCierreDAO {

    @Override
    public void insertar(ReporteCierre reporteCierre) throws SQLException {
        String sql = "INSERT INTO reporte_cierre (fecha_generacion, estado, nivel_final, id_estudiante, id_competencia, id_periodo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, reporteCierre);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(ReporteCierre reporteCierre) throws SQLException {
        String sql = "UPDATE reporte_cierre SET fecha_generacion = ?, estado = ?, nivel_final = ?, id_estudiante = ?, id_competencia = ?, id_periodo = ? WHERE id_reporte = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, reporteCierre);
            ps.setInt(7, reporteCierre.getIdReporte());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idReporte) throws SQLException {
        String sql = "DELETE FROM reporte_cierre WHERE id_reporte = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReporte);
            ps.executeUpdate();
        }
    }

    @Override
    public ReporteCierre buscarPorId(int idReporte) throws SQLException {
        List<ReporteCierre> lista = consultar("SELECT * FROM reporte_cierre WHERE id_reporte = ?", idReporte);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<ReporteCierre> listarTodos() throws SQLException {
        String sql = "SELECT * FROM reporte_cierre";
        List<ReporteCierre> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearReporteCierre(rs));
            }
        }
        return lista;
    }

    @Override
    public void cerrarRegistro(int idPeriodo) throws SQLException {
        String sql = "UPDATE reporte_cierre SET estado = 'Cerrado' WHERE id_periodo = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPeriodo);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ReporteCierre> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar("SELECT * FROM reporte_cierre WHERE id_estudiante = ? ORDER BY id_periodo", idEstudiante);
    }

    @Override
    public boolean estaCerrado(int idEstudiante, int idCompetencia, int idPeriodo) throws SQLException {
        String sql = "SELECT 1 FROM reporte_cierre WHERE id_estudiante = ? AND id_competencia = ? "
                + "AND id_periodo = ? AND estado = 'Cerrado'";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCompetencia);
            ps.setInt(3, idPeriodo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<ReporteCierre> consultar(String sql, int id) throws SQLException {
        List<ReporteCierre> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearReporteCierre(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param reporteCierre datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, ReporteCierre reporteCierre) throws SQLException {
        ps.setDate(1, reporteCierre.getFechaGeneracion() != null ? Date.valueOf(reporteCierre.getFechaGeneracion()) : null);
        ps.setString(2, reporteCierre.getEstado());
        ps.setString(3, reporteCierre.getNivelFinal());
        ps.setInt(4, reporteCierre.getIdEstudiante());
        ps.setInt(5, reporteCierre.getIdCompetencia());
        ps.setInt(6, reporteCierre.getIdPeriodo());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto ReporteCierre.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private ReporteCierre mapearReporteCierre(ResultSet rs) throws SQLException {
        ReporteCierre reporteCierre = new ReporteCierre();
        reporteCierre.setIdReporte(rs.getInt("id_reporte"));
        reporteCierre.setFechaGeneracion(rs.getDate("fecha_generacion") != null ? rs.getDate("fecha_generacion").toLocalDate() : null);
        reporteCierre.setEstado(rs.getString("estado"));
        reporteCierre.setNivelFinal(rs.getString("nivel_final"));
        reporteCierre.setIdEstudiante(rs.getInt("id_estudiante"));
        reporteCierre.setIdCompetencia(rs.getInt("id_competencia"));
        reporteCierre.setIdPeriodo(rs.getInt("id_periodo"));
        return reporteCierre;
    }
}