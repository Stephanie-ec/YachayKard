package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.DocenteDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Regla de acceso al historial de un estudiante (RF-03):
 * el administrador puede consultar a todos los estudiantes y el docente
 * solo a los estudiantes de las secciones donde es tutor.
 * <p>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class AccesoEstudiantes {

    private final EstudianteDAO estudianteDAO;
    private final DocenteDAO docenteDAO;
    private final SeccionDAO seccionDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public AccesoEstudiantes() {
        this(new EstudianteDAOImpl(), new DocenteDAOImpl(), new SeccionDAOImpl());
    }

    /**
     * Constructor para pruebas: recibe los DAO ya creados.
     *
     * @param estudianteDAO acceso a estudiantes
     * @param docenteDAO    acceso a docentes
     * @param seccionDAO    acceso a secciones
     */
    public AccesoEstudiantes(EstudianteDAO estudianteDAO, DocenteDAO docenteDAO, SeccionDAO seccionDAO) {
        this.estudianteDAO = estudianteDAO;
        this.docenteDAO = docenteDAO;
        this.seccionDAO = seccionDAO;
    }

    /**
     * Lista los estudiantes que el usuario puede consultar.
     *
     * @param usuario usuario que inició sesión
     * @param rol     nombre del rol del usuario
     * @return estudiantes permitidos (vacía si el rol no tiene acceso)
     * @throws SQLException si ocurre un error en la base de datos
     */
    public List<Estudiante> permitidos(Usuario usuario, String rol) throws SQLException {
        if (DatosSesion.ADMINISTRADOR.equals(rol)) {
            return estudianteDAO.listarTodos();
        }
        List<Estudiante> estudiantes = new ArrayList<>();
        if (DatosSesion.DOCENTE.equals(rol)) {
            for (Docente docente : docenteDAO.listarPorUsuario(usuario.getIdUsuario())) {
                for (Seccion seccion : seccionDAO.listarPorTutor(docente.getIdDocente())) {
                    estudiantes.addAll(estudianteDAO.listarPorSeccion(seccion.getIdSeccion()));
                }
            }
        }
        return estudiantes;
    }

    /**
     * Indica si el usuario puede consultar el historial de un estudiante.
     *
     * @param usuario      usuario que inició sesión
     * @param rol          nombre del rol del usuario
     * @param idEstudiante estudiante a consultar
     * @return true si es administrador o tutor de la sección del estudiante
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean puedeConsultar(Usuario usuario, String rol, int idEstudiante) throws SQLException {
        if (DatosSesion.ADMINISTRADOR.equals(rol)) {
            return true;
        }
        return permitidos(usuario, rol).stream()
                .anyMatch(e -> e.getIdEstudiante() == idEstudiante);
    }
}
