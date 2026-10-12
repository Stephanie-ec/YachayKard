package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link TipoIncidenciaDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class TipoIncidenciaDAOImpl implements TipoIncidenciaDAO {

    @Override
    public void insertar(TipoIncidencia tipoIncidencia) throws SQLException {
        String sql = "INSERT INTO tipo_incidencia (nombre, gravedad_sugerida) VALUES (?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, tipoIncidencia);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(TipoIncidencia tipoIncidencia) throws SQLException {
        String sql = "UPDATE tipo_incidencia SET nombre = ?, gravedad_sugerida = ? WHERE id_tipo_incidencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, tipoIncidencia);
            ps.setInt(3, tipoIncidencia.getIdTipoIncidencia());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idTipoIncidencia) throws SQLException {
        String sql = "DELETE FROM tipo_incidencia WHERE id_tipo_incidencia = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTipoIncidencia);
            ps.executeUpdate();
        }
    }

    @Override
    public TipoIncidencia buscarPorId(int idTipoIncidencia) throws SQLException {
        List<TipoIncidencia> lista = consultar("SELECT * FROM tipo_incidencia WHERE id_tipo_incidencia = ?", idTipoIncidencia);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<TipoIncidencia> listarTodos() throws SQLException {
        String sql = "SELECT * FROM tipo_incidencia";
        List<TipoIncidencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoIncidencia(rs));
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
    private List<TipoIncidencia> consultar(String sql, int id) throws SQLException {
        List<TipoIncidencia> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTipoIncidencia(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param tipoIncidencia datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, TipoIncidencia tipoIncidencia) throws SQLException {
        ps.setString(1, tipoIncidencia.getNombre());
        ps.setString(2, tipoIncidencia.getGravedadSugerida());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto TipoIncidencia.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private TipoIncidencia mapearTipoIncidencia(ResultSet rs) throws SQLException {
        TipoIncidencia tipoIncidencia = new TipoIncidencia();
        tipoIncidencia.setIdTipoIncidencia(rs.getInt("id_tipo_incidencia"));
        tipoIncidencia.setNombre(rs.getString("nombre"));
        tipoIncidencia.setGravedadSugerida(rs.getString("gravedad_sugerida"));
        return tipoIncidencia;
    }
}