package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.NotificacionDAO;
import pe.edu.utp.yachaykard.modelo.Notificacion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link NotificacionDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class NotificacionDAOImpl implements NotificacionDAO {

    @Override
    public void insertar(Notificacion notificacion) throws SQLException {
        String sql = "INSERT INTO notificacion (fecha_envio, mensaje, id_apoderado, id_usuario, id_estudiante, canal, tipo, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, notificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Notificacion notificacion) throws SQLException {
        String sql = "UPDATE notificacion SET fecha_envio = ?, mensaje = ?, id_apoderado = ?, id_usuario = ?, id_estudiante = ?, canal = ?, tipo = ?, estado = ? WHERE id_notificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, notificacion);
            ps.setInt(9, notificacion.getIdNotificacion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idNotificacion) throws SQLException {
        String sql = "DELETE FROM notificacion WHERE id_notificacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idNotificacion);
            ps.executeUpdate();
        }
    }

    @Override
    public Notificacion buscarPorId(int idNotificacion) throws SQLException {
        List<Notificacion> lista = consultar("SELECT * FROM notificacion WHERE id_notificacion = ?", idNotificacion);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Notificacion> listarTodos() throws SQLException {
        String sql = "SELECT * FROM notificacion";
        List<Notificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearNotificacion(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Notificacion> listarPorUsuario(int idUsuario) throws SQLException {
        return consultar("SELECT * FROM notificacion WHERE id_usuario = ? ORDER BY fecha_envio DESC", idUsuario);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Notificacion> consultar(String sql, int id) throws SQLException {
        List<Notificacion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearNotificacion(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param notificacion datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Notificacion notificacion) throws SQLException {
        ps.setTimestamp(1, notificacion.getFechaEnvio() != null ? Timestamp.valueOf(notificacion.getFechaEnvio()) : null);
        ps.setString(2, notificacion.getMensaje());
        setEnteroNulo(ps, 3, notificacion.getIdApoderado());
        setEnteroNulo(ps, 4, notificacion.getIdUsuario());
        ps.setInt(5, notificacion.getIdEstudiante());
        ps.setString(6, notificacion.getCanal());
        ps.setString(7, notificacion.getTipo());
        ps.setString(8, notificacion.getEstado());
    }

    /**
     * Asigna un entero que puede ser null (relación opcional 0..1).
     *
     * @param ps     sentencia preparada
     * @param indice posición del parámetro
     * @param valor  valor o null
     * @throws SQLException si falla la asignación
     */
    private void setEnteroNulo(PreparedStatement ps, int indice, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.INTEGER);
        } else {
            ps.setInt(indice, valor);
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Notificacion.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Notificacion mapearNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("id_notificacion"));
        notificacion.setFechaEnvio(rs.getTimestamp("fecha_envio") != null ? rs.getTimestamp("fecha_envio").toLocalDateTime() : null);
        notificacion.setMensaje(rs.getString("mensaje"));
        notificacion.setIdApoderado((Integer) rs.getObject("id_apoderado"));
        notificacion.setIdUsuario((Integer) rs.getObject("id_usuario"));
        notificacion.setIdEstudiante(rs.getInt("id_estudiante"));
        notificacion.setCanal(rs.getString("canal"));
        notificacion.setTipo(rs.getString("tipo"));
        notificacion.setEstado(rs.getString("estado"));
        return notificacion;
    }
}