package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.JustificacionDAO;
import pe.edu.utp.yachaykard.modelo.Justificacion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link JustificacionDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class JustificacionDAOImpl implements JustificacionDAO {

    @Override
    public void insertar(Justificacion justificacion) throws SQLException {
        String sql = "INSERT INTO justificacion (fecha_inicio, fecha_fin, motivo, evidencia, id_estudiante, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, justificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Justificacion justificacion) throws SQLException {
        String sql = "UPDATE justificacion SET fecha_inicio = ?, fecha_fin = ?, motivo = ?, evidencia = ?, id_estudiante = ?, id_usuario = ? WHERE id_justificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, justificacion);
            ps.setInt(7, justificacion.getIdJustificacion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idJustificacion) throws SQLException {
        String sql = "DELETE FROM justificacion WHERE id_justificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idJustificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public Justificacion buscarPorId(int idJustificacion) throws SQLException {
        List<Justificacion> lista = consultar("SELECT * FROM justificacion WHERE id_justificacion = ?", idJustificacion);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Justificacion> listarTodos() throws SQLException {
        String sql = "SELECT * FROM justificacion";
        List<Justificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearJustificacion(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Justificacion> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar("SELECT * FROM justificacion WHERE id_estudiante = ? ORDER BY fecha_inicio DESC", idEstudiante);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Justificacion> consultar(String sql, int id) throws SQLException {
        List<Justificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearJustificacion(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param justificacion datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Justificacion justificacion) throws SQLException {
        ps.setDate(1, justificacion.getFechaInicio() != null ? Date.valueOf(justificacion.getFechaInicio()) : null);
        ps.setDate(2, justificacion.getFechaFin() != null ? Date.valueOf(justificacion.getFechaFin()) : null);
        ps.setString(3, justificacion.getMotivo());
        ps.setString(4, justificacion.getEvidencia());
        ps.setInt(5, justificacion.getIdEstudiante());
        ps.setInt(6, justificacion.getIdUsuario());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Justificacion.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Justificacion mapearJustificacion(ResultSet rs) throws SQLException {
        Justificacion justificacion = new Justificacion();
        justificacion.setIdJustificacion(rs.getInt("id_justificacion"));
        justificacion.setFechaInicio(rs.getDate("fecha_inicio") != null ? rs.getDate("fecha_inicio").toLocalDate() : null);
        justificacion.setFechaFin(rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toLocalDate() : null);
        justificacion.setMotivo(rs.getString("motivo"));
        justificacion.setEvidencia(rs.getString("evidencia"));
        justificacion.setIdEstudiante(rs.getInt("id_estudiante"));
        justificacion.setIdUsuario(rs.getInt("id_usuario"));
        return justificacion;
    }
}