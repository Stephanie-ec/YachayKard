package pe.edu.utp.yachaykard.dao;

import pe.edu.utp.yachaykard.modelo.Institucion;

import java.sql.SQLException;
import java.util.List;

public interface InstitucionDAO {

    void insertar(Institucion institucion) throws SQLException;

    void actualizar(Institucion institucion) throws SQLException;

    void eliminar(int idInstitucion) throws SQLException;

    Institucion buscarPorId(int idInstitucion) throws SQLException;

    List<Institucion> listarTodos() throws SQLException;

}