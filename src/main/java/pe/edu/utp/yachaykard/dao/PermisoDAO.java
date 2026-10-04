package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Permiso;

import java.sql.SQLException;
import java.util.List;

public interface PermisoDAO {

    void insertar(Permiso permiso) throws SQLException;

    void actualizar(Permiso permiso) throws SQLException;

    void eliminar(int idPermiso) throws SQLException;

    Permiso buscarPorId(int idPermiso) throws SQLException;

    List<Permiso> listarTodos() throws SQLException;

}