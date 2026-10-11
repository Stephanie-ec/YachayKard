package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link EstudianteDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class EstudianteDAOImpl implements EstudianteDAO {

    @Override
    public void insertar(Estudiante estudiante) throws SQLException {
        String sql = "INSERT INTO estudiante (dni, nombres, apellidos, fecha_nacimiento, id_apoderado, id_siagie, codigo_estudiante) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, estudiante);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Estudiante estudiante) throws SQLException {
        String sql = "UPDATE estudiante SET dni = ?, nombres = ?, apellidos = ?, fecha_nacimiento = ?, id_apoderado = ?, id_siagie = ?, codigo_estudiante = ? WHERE id_estudiante = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, estudiante);
            ps.setInt(8, estudiante.getIdEstudiante());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idEstudiante) throws SQLException {
        String sql = "DELETE FROM estudiante WHERE id_estudiante = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.executeUpdate();
        }
    }

    @Override
    public Estudiante buscarPorId(int idEstudiante) throws SQLException {
        List<Estudiante> lista = consultar("SELECT * FROM estudiante WHERE id_estudiante = ?", idEstudiante);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Estudiante> listarTodos() throws SQLException {
        String sql = "SELECT * FROM estudiante";
        List<Estudiante> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEstudiante(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Estudiante> listarPorSeccion(int idSeccion) throws SQLException {
        return consultar("SELECT e.* FROM estudiante e JOIN matricula m ON m.id_estudiante = e.id_estudiante WHERE m.id_seccion = ? ORDER BY e.apellidos, e.nombres", idSeccion);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Estudiante> consultar(String sql, int id) throws SQLException {
        List<Estudiante> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearEstudiante(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param estudiante datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Estudiante estudiante) throws SQLException {
        ps.setString(1, estudiante.getDni());
        ps.setString(2, estudiante.getNombres());
        ps.setString(3, estudiante.getApellidos());
        ps.setDate(4, estudiante.getFechaNacimiento() != null ? Date.valueOf(estudiante.getFechaNacimiento()) : null);
        ps.setInt(5, estudiante.getIdApoderado());
        ps.setString(6, estudiante.getIdSiagie());
        ps.setString(7, estudiante.getCodigoEstudiante());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Estudiante.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(rs.getInt("id_estudiante"));
        estudiante.setDni(rs.getString("dni"));
        estudiante.setNombres(rs.getString("nombres"));
        estudiante.setApellidos(rs.getString("apellidos"));
        estudiante.setFechaNacimiento(rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toLocalDate() : null);
        estudiante.setIdApoderado(rs.getInt("id_apoderado"));
        estudiante.setIdSiagie(rs.getString("id_siagie"));
        estudiante.setCodigoEstudiante(rs.getString("codigo_estudiante"));
        return estudiante;
    }
}
