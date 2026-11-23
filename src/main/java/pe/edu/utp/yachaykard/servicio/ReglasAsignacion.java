package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.AsignacionCursoDAO;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Reglas de la asignación docente – curso – sección (RF-08):
 * <ul>
 *     <li>Un curso tiene un solo docente por sección y año.</li>
 *     <li>Las competencias transversales (hojas 0006 y 0007 de RegNotas) son la nota de Tutoría:
 *         solo se asignan al tutor de la sección.</li>
 * </ul>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class ReglasAsignacion {

    /** Códigos de las hojas de competencias transversales en RegNotas. */
    static final Set<String> TRANSVERSALES = Set.of("0006", "0007");
    private static final Pattern ANIO = Pattern.compile("\\d{4}");

    private final AsignacionCursoDAO asignacionDAO;
    private final CursoDAO cursoDAO;
    private final SeccionDAO seccionDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public ReglasAsignacion() {
        this(new AsignacionCursoDAOImpl(), new CursoDAOImpl(), new SeccionDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param asignacionDAO acceso a asignaciones
     * @param cursoDAO      acceso a cursos
     * @param seccionDAO    acceso a secciones
     */
    public ReglasAsignacion(AsignacionCursoDAO asignacionDAO, CursoDAO cursoDAO, SeccionDAO seccionDAO) {
        this.asignacionDAO = asignacionDAO;
        this.cursoDAO = cursoDAO;
        this.seccionDAO = seccionDAO;
    }

    /**
     * Registra una asignación si cumple las reglas.
     *
     * @param asignacion docente, curso, sección y año académico
     * @throws IllegalArgumentException si falta un dato o se rompe una regla
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void registrar(AsignacionCurso asignacion) throws SQLException {
        if (asignacion.getIdDocente() <= 0 || asignacion.getIdCurso() <= 0 || asignacion.getIdSeccion() <= 0) {
            throw new IllegalArgumentException("Seleccione el docente, el curso y la sección.");
        }
        if (asignacion.getAnioAcademico() == null || !ANIO.matcher(asignacion.getAnioAcademico()).matches()) {
            throw new IllegalArgumentException("El año académico debe tener 4 dígitos.");
        }
        boolean ocupado = asignacionDAO.listarTodos().stream()
                .anyMatch(a -> a.getIdCurso() == asignacion.getIdCurso()
                        && a.getIdSeccion() == asignacion.getIdSeccion()
                        && asignacion.getAnioAcademico().equals(a.getAnioAcademico()));
        if (ocupado) {
            throw new IllegalArgumentException("Ese curso ya tiene un docente asignado en la sección este año.");
        }
        Curso curso = cursoDAO.buscarPorId(asignacion.getIdCurso());
        if (curso != null && esTransversal(curso)) {
            Seccion seccion = seccionDAO.buscarPorId(asignacion.getIdSeccion());
            if (seccion == null || !Objects.equals(seccion.getIdDocenteTutor(), asignacion.getIdDocente())) {
                throw new IllegalArgumentException(
                        "Las competencias transversales (Tutoría) solo se asignan al tutor de la sección.");
            }
        }
        asignacionDAO.insertar(asignacion);
    }

    /**
     * @param curso curso a revisar
     * @return true si es una competencia transversal (hojas 0006 o 0007)
     */
    public boolean esTransversal(Curso curso) {
        return curso.getCodigoSiagie() != null && TRANSVERSALES.contains(curso.getCodigoSiagie());
    }
}