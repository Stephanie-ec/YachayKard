package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.AsignacionCursoDAO;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link AsignacionCursoDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class AsignacionCursoDAOImpl implements AsignacionCursoDAO {

    @Override
    public void insertar(AsignacionCurso asignacionCurso) throws SQLException {
        String sql = "INSERT INTO asignacion_curso (anio_academico, id_docente, id_curso, id_seccion) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, asignacionCurso);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(AsignacionCurso asignacionCurso) throws SQLException {
        String sql = "UPDATE asignacion_curso SET anio_academico = ?, id_docente = ?, id_curso = ?, id_seccion = ? WHERE id_asignacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, asignacionCurso);
            ps.setInt(5, asignacionCurso.getIdAsignacion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idAsignacion) throws SQLException {
        String sql = "DELETE FROM asignacion_curso WHERE id_asignacion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);
            ps.executeUpdate();
        }
    }

    @Override
    public AsignacionCurso buscarPorId(int idAsignacion) throws SQLException {
        List<AsignacionCurso> lista = consultar("SELECT * FROM asignacion_curso WHERE id_asignacion = ?", idAsignacion);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<AsignacionCurso> listarTodos() throws SQLException {
        String sql = "SELECT * FROM asignacion_curso";
        List<AsignacionCurso> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAsignacionCurso(rs));
            }
        }
        return lista;
    }

    @Override
    public List<AsignacionCurso> listarPorDocente(int idDocente) throws SQLException {
        return consultar("SELECT * FROM asignacion_curso WHERE id_docente = ?", idDocente);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<AsignacionCurso> consultar(String sql, int id) throws SQLException {
        List<AsignacionCurso> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsignacionCurso(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps              sentencia preparada
     * @param asignacionCurso datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, AsignacionCurso asignacionCurso) throws SQLException {
        ps.setString(1, asignacionCurso.getAnioAcademico());
        ps.setInt(2, asignacionCurso.getIdDocente());
        ps.setInt(3, asignacionCurso.getIdCurso());
        ps.setInt(4, asignacionCurso.getIdSeccion());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto AsignacionCurso.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private AsignacionCurso mapearAsignacionCurso(ResultSet rs) throws SQLException {
        AsignacionCurso asignacionCurso = new AsignacionCurso();
        asignacionCurso.setIdAsignacion(rs.getInt("id_asignacion"));
        asignacionCurso.setAnioAcademico(rs.getString("anio_academico"));
        asignacionCurso.setIdDocente(rs.getInt("id_docente"));
        asignacionCurso.setIdCurso(rs.getInt("id_curso"));
        asignacionCurso.setIdSeccion(rs.getInt("id_seccion"));
        return asignacionCurso;
    }
}