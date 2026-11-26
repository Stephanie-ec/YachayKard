package pe.edu.utp.yachaykard.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Consultas para saber a quién se envían las alertas internas (RF-27):
 * los administradores activos y el tutor de la sección de cada estudiante.
 */
public interface DestinatariosDAO {

    /**
     * Estudiante con matrícula activa y el usuario del tutor de su sección.
     *
     * @param idEstudiante   estudiante
     * @param estudiante     "Apellidos, Nombres"
     * @param idUsuarioTutor usuario del tutor; null si la sección no tiene tutor
     */
    record Destino(int idEstudiante, String estudiante, Integer idUsuarioTutor) {
    }

    /**
     * @return ids de usuario de los administradores activos
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Integer> listarAdministradores() throws SQLException;

    /**
     * @param anioLectivo año de las matrículas (por ejemplo "2026")
     * @return un destino por estudiante con matrícula activa en ese año
     * @throws SQLException si ocurre un error en la base de datos
     */
    List<Destino> listarDestinos(String anioLectivo) throws SQLException;
}