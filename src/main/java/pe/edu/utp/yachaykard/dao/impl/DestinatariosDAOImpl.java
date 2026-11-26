package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.DestinatariosDAO;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link DestinatariosDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class DestinatariosDAOImpl implements DestinatariosDAO {

    @Override
    public List<Integer> listarAdministradores() throws SQLException {
        String sql = "SELECT u.id_usuario FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
                + "WHERE r.nombre_rol = 'Administrador' AND u.activo = 1";
        List<Integer> ids = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id_usuario"));
            }
        }
        return ids;
    }

    @Override
    public List<Destino> listarDestinos(String anioLectivo) throws SQLException {
        String sql = "SELECT e.id_estudiante, e.apellidos, e.nombres, d.id_usuario AS id_usuario_tutor "
                + "FROM matricula m "
                + "JOIN estudiante e ON e.id_estudiante = m.id_estudiante "
                + "JOIN seccion s ON s.id_seccion = m.id_seccion "
                + "LEFT JOIN docente d ON d.id_docente = s.id_docente_tutor "
                + "WHERE m.estado = 'Activa' AND m.anio_lectivo = ? "
                + "ORDER BY e.apellidos, e.nombres";
        List<Destino> destinos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, anioLectivo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    destinos.add(new Destino(rs.getInt("id_estudiante"),
                            rs.getString("apellidos") + ", " + rs.getString("nombres"),
                            (Integer) rs.getObject("id_usuario_tutor")));
                }
            }
        }
        return destinos;
    }
}