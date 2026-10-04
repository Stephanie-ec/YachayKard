package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {

    void insertar(Usuario usuario) throws SQLException;

    void actualizar(Usuario usuario) throws SQLException;

    void eliminar(int idUsuario) throws SQLException;

    Usuario buscarPorId(int idUsuario) throws SQLException;

    List<Usuario> listarTodos() throws SQLException;

}