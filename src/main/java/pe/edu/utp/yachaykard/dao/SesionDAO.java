package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Sesion;

import java.sql.SQLException;
import java.util.List;

public interface SesionDAO {

    void insertar(Sesion sesion) throws SQLException;

    void actualizar(Sesion sesion) throws SQLException;

    void eliminar(int idSesion) throws SQLException;

    Sesion buscarPorId(int idSesion) throws SQLException;

    List<Sesion> listarTodos() throws SQLException;

}