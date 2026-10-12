package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link AsistenciaDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class AsistenciaDAOImpl implements AsistenciaDAO {

    @Override
    public void insertar(Asistencia asistencia) throws SQLException {
        String sql = "INSERT INTO asistencia (fecha, estado, id_estudiante, id_auxiliar, id_justificacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, asistencia);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Asistencia asistencia) throws SQLException {
        String sql = "UPDATE asistencia SET fecha = ?, estado = ?, id_estudiante = ?, id_auxiliar = ?, id_justificacion = ? WHERE id_asistencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, asistencia);
            ps.setInt(6, asistencia.getIdAsistencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idAsistencia) throws SQLException {
        String sql = "DELETE FROM asistencia WHERE id_asistencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsistencia);
            ps.executeUpdate();
        }
    }

    @Override
    public Asistencia buscarPorId(int idAsistencia) throws SQLException {
        List<Asistencia> lista = consultar("SELECT * FROM asistencia WHERE id_asistencia = ?", idAsistencia);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Asistencia> listarTodos() throws SQLException {
        String sql = "SELECT * FROM asistencia";
        List<Asistencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAsistencia(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Asistencia> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar("SELECT * FROM asistencia WHERE id_estudiante = ? ORDER BY fecha DESC", idEstudiante);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Asistencia> consultar(String sql, int id) throws SQLException {
        List<Asistencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param asistencia datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Asistencia asistencia) throws SQLException {
        ps.setDate(1, asistencia.getFecha() != null ? Date.valueOf(asistencia.getFecha()) : null);
        ps.setString(2, asistencia.getEstado());
        ps.setInt(3, asistencia.getIdEstudiante());
        ps.setInt(4, asistencia.getIdAuxiliar());
        setEnteroNulo(ps, 5, asistencia.getIdJustificacion());
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
     * Convierte la fila actual del ResultSet en un objeto Asistencia.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Asistencia mapearAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setFecha(rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null);
        asistencia.setEstado(rs.getString("estado"));
        asistencia.setIdEstudiante(rs.getInt("id_estudiante"));
        asistencia.setIdAuxiliar(rs.getInt("id_auxiliar"));
        asistencia.setIdJustificacion((Integer) rs.getObject("id_justificacion"));
        return asistencia;
    }
}