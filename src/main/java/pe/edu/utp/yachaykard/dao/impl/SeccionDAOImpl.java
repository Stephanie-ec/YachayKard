package pe.edu.utp.yachaykard.dao.impl;

import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de {@link SeccionDAO}.
 * Cada método abre su conexión con try-with-resources, que la cierra automáticamente.
 */
public class SeccionDAOImpl implements SeccionDAO {

    @Override
    public void insertar(Seccion seccion) throws SQLException {
        String sql = "INSERT INTO seccion (letra, aforo, id_grado, id_docente_tutor, id_auxiliar) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, seccion);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Seccion seccion) throws SQLException {
        String sql = "UPDATE seccion SET letra = ?, aforo = ?, id_grado = ?, id_docente_tutor = ?, id_auxiliar = ? WHERE id_seccion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            asignarParametros(ps, seccion);
            ps.setInt(6, seccion.getIdSeccion());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idSeccion) throws SQLException {
        String sql = "DELETE FROM seccion WHERE id_seccion = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);
            ps.executeUpdate();
        }
    }

    @Override
    public Seccion buscarPorId(int idSeccion) throws SQLException {
        List<Seccion> lista = consultar("SELECT * FROM seccion WHERE id_seccion = ?", idSeccion);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Seccion> listarTodos() throws SQLException {
        String sql = "SELECT * FROM seccion";
        List<Seccion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearSeccion(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Seccion> listarPorTutor(int idDocenteTutor) throws SQLException {
        return consultar("SELECT * FROM seccion WHERE id_docente_tutor = ?", idDocenteTutor);
    }

    /**
     * Ejecuta una consulta con un único parámetro entero y devuelve las filas mapeadas.
     *
     * @param sql consulta con un parámetro
     * @param id  valor del parámetro
     * @return registros encontrados
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Seccion> consultar(String sql, int id) throws SQLException {
        List<Seccion> lista = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearSeccion(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Asigna los campos comunes de INSERT y UPDATE, en el orden de las columnas.
     *
     * @param ps sentencia preparada
     * @param seccion datos de origen
     * @throws SQLException si falla la asignación de un parámetro
     */
    private void asignarParametros(PreparedStatement ps, Seccion seccion) throws SQLException {
        ps.setString(1, String.valueOf(seccion.getLetra()));
        ps.setInt(2, seccion.getAforo());
        ps.setInt(3, seccion.getIdGrado());
        setEnteroNulo(ps, 4, seccion.getIdDocenteTutor());
        setEnteroNulo(ps, 5, seccion.getIdAuxiliar());
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
     * Convierte la fila actual del ResultSet en un objeto Seccion.
     *
     * @param rs resultado posicionado en una fila
     * @return objeto con los datos de la fila
     * @throws SQLException si una columna no existe
     */
    private Seccion mapearSeccion(ResultSet rs) throws SQLException {
        Seccion seccion = new Seccion();
        seccion.setIdSeccion(rs.getInt("id_seccion"));
        seccion.setLetra(rs.getString("letra").charAt(0));
        seccion.setAforo(rs.getInt("aforo"));
        seccion.setIdGrado(rs.getInt("id_grado"));
        seccion.setIdDocenteTutor((Integer) rs.getObject("id_docente_tutor"));
        seccion.setIdAuxiliar((Integer) rs.getObject("id_auxiliar"));
        return seccion;
    }
}
