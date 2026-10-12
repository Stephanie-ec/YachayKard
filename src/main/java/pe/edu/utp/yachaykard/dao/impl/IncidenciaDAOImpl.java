package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.modelo.Incidencia;
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
 * Implementación JDBC de {@link IncidenciaDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class IncidenciaDAOImpl implements IncidenciaDAO {

    @Override
    public void insertar(Incidencia incidencia) throws SQLException {
        String sql = "INSERT INTO incidencia (fecha_hora, descripcion, id_estudiante, id_tipo_incidencia, gravedad, accion_tomada, id_usuario, id_curso, requiere_siseve) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, incidencia);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Incidencia incidencia) throws SQLException {
        String sql = "UPDATE incidencia SET fecha_hora = ?, descripcion = ?, id_estudiante = ?, id_tipo_incidencia = ?, gravedad = ?, accion_tomada = ?, id_usuario = ?, id_curso = ?, requiere_siseve = ? WHERE id_incidencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, incidencia);
            ps.setInt(10, incidencia.getIdIncidencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idIncidencia) throws SQLException {
        String sql = "DELETE FROM incidencia WHERE id_incidencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idIncidencia);
            ps.executeUpdate();
        }
    }

    @Override
    public Incidencia buscarPorId(int idIncidencia) throws SQLException {
        List<Incidencia> lista = consultar("SELECT * FROM incidencia WHERE id_incidencia = ?", idIncidencia);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Incidencia> listarTodos() throws SQLException {
        String sql = "SELECT * FROM incidencia";
        List<Incidencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearIncidencia(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Incidencia> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar("SELECT * FROM incidencia WHERE id_estudiante = ? ORDER BY fecha_hora DESC", idEstudiante);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Incidencia> consultar(String sql, int id) throws SQLException {
        List<Incidencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearIncidencia(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param incidencia datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Incidencia incidencia) throws SQLException {
        ps.setTimestamp(1, incidencia.getFechaHora() != null ? Timestamp.valueOf(incidencia.getFechaHora()) : null);
        ps.setString(2, incidencia.getDescripcion());
        ps.setInt(3, incidencia.getIdEstudiante());
        ps.setInt(4, incidencia.getIdTipoIncidencia());
        ps.setString(5, incidencia.getGravedad());
        ps.setString(6, incidencia.getAccionTomada());
        ps.setInt(7, incidencia.getIdUsuario());
        setEnteroNulo(ps, 8, incidencia.getIdCurso());
        ps.setBoolean(9, incidencia.isRequiereSiseve());
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
     * Convierte la fila actual del ResultSet en un objeto Incidencia.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Incidencia mapearIncidencia(ResultSet rs) throws SQLException {
        Incidencia incidencia = new Incidencia();
        incidencia.setIdIncidencia(rs.getInt("id_incidencia"));
        incidencia.setFechaHora(rs.getTimestamp("fecha_hora") != null ? rs.getTimestamp("fecha_hora").toLocalDateTime() : null);
        incidencia.setDescripcion(rs.getString("descripcion"));
        incidencia.setIdEstudiante(rs.getInt("id_estudiante"));
        incidencia.setIdTipoIncidencia(rs.getInt("id_tipo_incidencia"));
        incidencia.setGravedad(rs.getString("gravedad"));
        incidencia.setAccionTomada(rs.getString("accion_tomada"));
        incidencia.setIdUsuario(rs.getInt("id_usuario"));
        incidencia.setIdCurso((Integer) rs.getObject("id_curso"));
        incidencia.setRequiereSiseve(rs.getBoolean("requiere_siseve"));
        return incidencia;
    }
}