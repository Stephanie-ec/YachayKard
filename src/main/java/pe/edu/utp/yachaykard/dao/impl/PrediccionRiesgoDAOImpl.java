package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.PrediccionRiesgoDAO;
import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link PrediccionRiesgoDAO}.
 * Lee y guarda la fecha como LocalDate directamente con getObject() y setObject().
 */
public class PrediccionRiesgoDAOImpl implements PrediccionRiesgoDAO {

    /** Columnas que se leen de la tabla prediccion_riesgo. */
    private static final String COLUMNAS =
            "id_prediccion, id_estudiante, nivel_riesgo, motivo, fecha_calculo";

    /** Inicio común de las consultas de esta tabla. */
    private static final String CONSULTA_BASE = "SELECT " + COLUMNAS + " FROM prediccion_riesgo";

    @Override
    public void insertar(PrediccionRiesgo prediccion) throws SQLException {
        String sql = "INSERT INTO prediccion_riesgo (id_estudiante, nivel_riesgo, motivo, fecha_calculo) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, prediccion);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(PrediccionRiesgo prediccion) throws SQLException {
        String sql = "UPDATE prediccion_riesgo SET id_estudiante = ?, nivel_riesgo = ?, motivo = ?, " +
                "fecha_calculo = ? WHERE id_prediccion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, prediccion);
            ps.setInt(5, prediccion.getIdPrediccion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idPrediccion) throws SQLException {
        String sql = "DELETE FROM prediccion_riesgo WHERE id_prediccion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPrediccion);
            ps.executeUpdate();
        }
    }

    @Override
    public PrediccionRiesgo buscarPorId(int idPrediccion) throws SQLException {
        List<PrediccionRiesgo> lista = consultar(
                CONSULTA_BASE + " WHERE id_prediccion = ?", idPrediccion);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<PrediccionRiesgo> listarTodos() throws SQLException {
        String sql = CONSULTA_BASE + " ORDER BY fecha_calculo DESC";
        List<PrediccionRiesgo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPrediccion(rs));
            }
        }
        return lista;
    }

    @Override
    public List<PrediccionRiesgo> listarPorEstudiante(int idEstudiante) throws SQLException {
        return consultar(CONSULTA_BASE + " WHERE id_estudiante = ? " +
                "ORDER BY fecha_calculo DESC, id_prediccion DESC", idEstudiante);
    }

    @Override
    public PrediccionRiesgo buscarUltimaPorEstudiante(int idEstudiante) throws SQLException {
        List<PrediccionRiesgo> lista = consultar(CONSULTA_BASE + " WHERE id_estudiante = ? " +
                "ORDER BY fecha_calculo DESC, id_prediccion DESC LIMIT 1", idEstudiante);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<PrediccionRiesgo> listarVigentesPorNivel(String nivelRiesgo) throws SQLException {
        String sql = "SELECT p.* FROM prediccion_riesgo p " +
                "WHERE p.nivel_riesgo = ? AND p.id_prediccion = (" +
                "  SELECT p2.id_prediccion FROM prediccion_riesgo p2 " +
                "  WHERE p2.id_estudiante = p.id_estudiante " +
                "  ORDER BY p2.fecha_calculo DESC, p2.id_prediccion DESC LIMIT 1)";
        List<PrediccionRiesgo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nivelRiesgo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrediccion(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return predicciones encontradas
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<PrediccionRiesgo> consultar(String sql, int id) throws SQLException {
        List<PrediccionRiesgo> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrediccion(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los 4 campos comunes de INSERT y UPDATE.
     *
     * @param ps         sentencia preparada
     * @param prediccion datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, PrediccionRiesgo prediccion) throws SQLException {
        ps.setInt(1, prediccion.getIdEstudiante());
        ps.setString(2, prediccion.getNivelRiesgo());
        ps.setString(3, prediccion.getMotivo());
        ps.setObject(4, prediccion.getFechaCalculo());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto PrediccionRiesgo.
     *
     * @param rs resultado posicionado en una fila
     * @return predicción con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private PrediccionRiesgo mapearPrediccion(ResultSet rs) throws SQLException {
        PrediccionRiesgo prediccion = new PrediccionRiesgo();
        prediccion.setIdPrediccion(rs.getInt("id_prediccion"));
        prediccion.setIdEstudiante(rs.getInt("id_estudiante"));
        prediccion.setNivelRiesgo(rs.getString("nivel_riesgo"));
        prediccion.setMotivo(rs.getString("motivo"));
        prediccion.setFechaCalculo(rs.getObject("fecha_calculo", LocalDate.class));
        return prediccion;
    }
}