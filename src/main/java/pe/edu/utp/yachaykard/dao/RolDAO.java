package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Rol;

import java.sql.SQLException;
import java.util.List;

public interface RolDAO {

    void insertar(Rol rol) throws SQLException;

    void actualizar(Rol rol) throws SQLException;

    void eliminar(int idRol) throws SQLException;

    Rol buscarPorId(int idRol) throws SQLException;

    List<Rol> listarTodos() throws SQLException;

}