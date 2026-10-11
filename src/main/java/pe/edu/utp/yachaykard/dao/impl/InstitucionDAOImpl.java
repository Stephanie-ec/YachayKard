package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.InstitucionDAO;
import pe.edu.utp.yachaykard.modelo.Institucion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link InstitucionDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class InstitucionDAOImpl implements InstitucionDAO {

    @Override
    public void insertar(Institucion institucion) throws SQLException {
        String sql = "INSERT INTO institucion (nombre, codigo_modular, nivel, nombre_director, direccion, " +
                "departamento, provincia, distrito, telefono, correo_electronico) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, institucion);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Institucion institucion) throws SQLException {
        String sql = "UPDATE institucion SET nombre = ?, codigo_modular = ?, nivel = ?, nombre_director = ?, " +
                "direccion = ?, departamento = ?, provincia = ?, distrito = ?, telefono = ?, " +
                "correo_electronico = ? WHERE id_institucion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, institucion);
            ps.setInt(11, institucion.getIdInstitucion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idInstitucion) throws SQLException {
        String sql = "DELETE FROM institucion WHERE id_institucion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInstitucion);
            ps.executeUpdate();
        }
    }

    @Override
    public Institucion buscarPorId(int idInstitucion) throws SQLException {
        String sql = "SELECT * FROM institucion WHERE id_institucion = ?";
        Institucion institucion = null;

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInstitucion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    institucion = mapearInstitucion(rs);
                }
            }
        }
        return institucion;
    }

    @Override
    public List<Institucion> listarTodos() throws SQLException {
        String sql = "SELECT * FROM institucion";
        List<Institucion> instituciones = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                instituciones.add(mapearInstitucion(rs));
            }
        }
        return instituciones;
    }

    /**
     * Asigna los 10 campos comunes de INSERT y UPDATE, en el mismo orden de las columnas.
     *
     * @param ps          sentencia preparada
     * @param institucion datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Institucion institucion) throws SQLException {
        ps.setString(1, institucion.getNombre());
        ps.setString(2, institucion.getCodigoModular());
        ps.setString(3, institucion.getNivel());
        ps.setString(4, institucion.getNombreDirector());
        ps.setString(5, institucion.getDireccion());
        ps.setString(6, institucion.getDepartamento());
        ps.setString(7, institucion.getProvincia());
        ps.setString(8, institucion.getDistrito());
        ps.setString(9, institucion.getTelefono());
        ps.setString(10, institucion.getCorreoElectronico());
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto Institucion.
     *
     * @param rs resultado posicionado en una fila
     * @return institución con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Institucion mapearInstitucion(ResultSet rs) throws SQLException {
        Institucion institucion = new Institucion();
        institucion.setIdInstitucion(rs.getInt("id_institucion"));
        institucion.setNombre(rs.getString("nombre"));
        institucion.setCodigoModular(rs.getString("codigo_modular"));
        institucion.setNivel(rs.getString("nivel"));
        institucion.setNombreDirector(rs.getString("nombre_director"));
        institucion.setDireccion(rs.getString("direccion"));
        institucion.setDepartamento(rs.getString("departamento"));
        institucion.setProvincia(rs.getString("provincia"));
        institucion.setDistrito(rs.getString("distrito"));
        institucion.setTelefono(rs.getString("telefono"));
        institucion.setCorreoElectronico(rs.getString("correo_electronico"));
        return institucion;
    }
}