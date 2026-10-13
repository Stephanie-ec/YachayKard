package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.PeriodoDAO;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link PeriodoDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class PeriodoDAOImpl implements PeriodoDAO {

    @Override
    public void insertar(Periodo periodo) throws SQLException {
        String sql = "INSERT INTO periodo (nombre_periodo, tipo, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, periodo);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Periodo periodo) throws SQLException {
        String sql = "UPDATE periodo SET nombre_periodo = ?, tipo = ?, fecha_inicio = ?, fecha_fin = ?, estado = ? WHERE id_periodo = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, periodo);
            ps.setInt(6, periodo.getIdPeriodo());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idPeriodo) throws SQLException {
        String sql = "DELETE FROM periodo WHERE id_periodo = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPeriodo);
            ps.executeUpdate();
        }
    }

    @Override
    public Periodo buscarPorId(int idPeriodo) throws SQLException {
        List<Periodo> lista = consultar("SELECT * FROM periodo WHERE id_periodo = ?", idPeriodo);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Periodo> listarTodos() throws SQLException {
        String sql = "SELECT * FROM periodo";
        List<Periodo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPeriodo(rs));
            }
        }
        return lista;
    }

    @Override
    public void abrirPeriodo(int idPeriodo) throws SQLException {
        String cerrarOtros = "UPDATE periodo SET estado = 'Cerrado' WHERE estado = 'Abierto' AND id_periodo <> ?";
        String abrir = "UPDATE periodo SET estado = 'Abierto' WHERE id_periodo = ?";

        // Las dos actualizaciones van en una transacción: o se aplican ambas o ninguna.
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(cerrarOtros);
                 PreparedStatement ps2 = con.prepareStatement(abrir)) {
                ps1.setInt(1, idPeriodo);
                ps1.executeUpdate();
                ps2.setInt(1, idPeriodo);
                ps2.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    @Override
    public void cerrarPeriodo(int idPeriodo) throws SQLException {
        cambiarEstado(idPeriodo, "Cerrado");
    }

    /**
     * Cambia el estado de un periodo.
     *
     * @param idPeriodo periodo a modificar
     * @param estado    Abierto o Cerrado
     * @throws SQLException si ocurre un error en la base de datos
     */
    private void cambiarEstado(int idPeriodo, String estado) throws SQLException {
        String sql = "UPDATE periodo SET estado = ? WHERE id_periodo = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idPeriodo);
            ps.executeUpdate();
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
    private List<Periodo> consultar(String sql, int id) throws SQLException {
        List<Periodo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPeriodo(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param periodo datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Periodo periodo) throws SQLException {
        ps.setString(1, periodo.getNombrePeriodo());
        ps.setString(2, periodo.getTipo());
        ps.setDate(3, periodo.getFechaInicio() != null ? Date.valueOf(periodo.getFechaInicio()) : null);
        ps.setDate(4, periodo.getFechaFin() != null ? Date.valueOf(periodo.getFechaFin()) : null);
        ps.setString(5, periodo.getEstado());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Periodo.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Periodo mapearPeriodo(ResultSet rs) throws SQLException {
        Periodo periodo = new Periodo();
        periodo.setIdPeriodo(rs.getInt("id_periodo"));
        periodo.setNombrePeriodo(rs.getString("nombre_periodo"));
        periodo.setTipo(rs.getString("tipo"));
        periodo.setFechaInicio(rs.getDate("fecha_inicio") != null ? rs.getDate("fecha_inicio").toLocalDate() : null);
        periodo.setFechaFin(rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null);
        periodo.setEstado(rs.getString("estado"));
        return periodo;
    }
}