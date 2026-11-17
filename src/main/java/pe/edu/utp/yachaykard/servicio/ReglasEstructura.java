package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.GradoDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;

/**
 * Reglas de la estructura académica: grados (RF-05), secciones (RF-06) y cursos (RF-09).
 * <ul>
 *     <li>No se repite un grado del mismo nivel.</li>
 *     <li>La letra de la sección va de la A a la Z y no se repite dentro del grado; el aforo es de 1 a 45.</li>
 *     <li>No se repite el nombre de un curso; sus horas semanales van de 0 a 10.</li>
 * </ul>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class ReglasEstructura {

    static final int AFORO_MAXIMO = 45;
    static final int HORAS_MAXIMAS = 10;

    private final GradoDAO gradoDAO;
    private final SeccionDAO seccionDAO;
    private final CursoDAO cursoDAO;

    /**
     * Constructor que usa la aplicación: trabaja con la base de datos.
     */
    public ReglasEstructura() {
        this(new GradoDAOImpl(), new SeccionDAOImpl(), new CursoDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param gradoDAO   acceso a grados
     * @param seccionDAO acceso a secciones
     * @param cursoDAO   acceso a cursos
     */
    public ReglasEstructura(GradoDAO gradoDAO, SeccionDAO seccionDAO, CursoDAO cursoDAO) {
        this.gradoDAO = gradoDAO;
        this.seccionDAO = seccionDAO;
        this.cursoDAO = cursoDAO;
    }

    /**
     * Registra un grado nuevo.
     *
     * @param grado número de grado (por ejemplo 1°) y nivel
     * @throws IllegalArgumentException si falta un dato o el grado ya existe
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void guardarGrado(Grado grado) throws SQLException {
        if (vacio(grado.getNumeroGrado()) || vacio(grado.getNivel())) {
            throw new IllegalArgumentException("Ingrese el grado y el nivel.");
        }
        grado.setNumeroGrado(grado.getNumeroGrado().trim());
        boolean repetido = gradoDAO.listarTodos().stream()
                .anyMatch(g -> g.getIdGrado() != grado.getIdGrado()
                        && g.getNumeroGrado().equalsIgnoreCase(grado.getNumeroGrado())
                        && g.getNivel().equalsIgnoreCase(grado.getNivel()));
        if (repetido) {
            throw new IllegalArgumentException("Ese grado ya existe en el nivel " + grado.getNivel() + ".");
        }
        if (grado.getIdGrado() == 0) {
            gradoDAO.insertar(grado);
        } else {
            gradoDAO.actualizar(grado);
        }
    }

    /**
     * Registra o actualiza una sección.
     *
     * @param seccion grado, letra, aforo, tutor y auxiliar (tutor y auxiliar son opcionales)
     * @throws IllegalArgumentException si un dato no cumple las reglas
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void guardarSeccion(Seccion seccion) throws SQLException {
        if (seccion.getIdGrado() <= 0) {
            throw new IllegalArgumentException("Seleccione el grado de la sección.");
        }
        char letra = Character.toUpperCase(seccion.getLetra());
        if (letra < 'A' || letra > 'Z') {
            throw new IllegalArgumentException("La letra de la sección debe ir de la A a la Z.");
        }
        seccion.setLetra(letra);
        if (seccion.getAforo() < 1 || seccion.getAforo() > AFORO_MAXIMO) {
            throw new IllegalArgumentException("El aforo debe estar entre 1 y " + AFORO_MAXIMO + " estudiantes.");
        }
        boolean repetida = seccionDAO.listarTodos().stream()
                .anyMatch(s -> s.getIdSeccion() != seccion.getIdSeccion()
                        && s.getIdGrado() == seccion.getIdGrado()
                        && Character.toUpperCase(s.getLetra()) == letra);
        if (repetida) {
            throw new IllegalArgumentException("Ya existe la sección \"" + letra + "\" en ese grado.");
        }
        if (seccion.getIdDocenteTutor() != null && seccion.getIdDocenteTutor() <= 0) {
            seccion.setIdDocenteTutor(null);
        }
        if (seccion.getIdAuxiliar() != null && seccion.getIdAuxiliar() <= 0) {
            seccion.setIdAuxiliar(null);
        }
        if (seccion.getIdSeccion() == 0) {
            seccionDAO.insertar(seccion);
        } else {
            seccionDAO.actualizar(seccion);
        }
    }

    /**
     * Registra o actualiza un curso.
     *
     * @param curso nombre, horas semanales y código de la hoja de RegNotas (opcional)
     * @throws IllegalArgumentException si un dato no cumple las reglas
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void guardarCurso(Curso curso) throws SQLException {
        if (vacio(curso.getNombreCurso())) {
            throw new IllegalArgumentException("Ingrese el nombre del curso.");
        }
        curso.setNombreCurso(curso.getNombreCurso().trim());
        if (curso.getHorasSemanales() < 0 || curso.getHorasSemanales() > HORAS_MAXIMAS) {
            throw new IllegalArgumentException("Las horas semanales deben estar entre 0 y " + HORAS_MAXIMAS + ".");
        }
        if (vacio(curso.getCodigoSiagie())) {
            curso.setCodigoSiagie(null);
        }
        boolean repetido = cursoDAO.listarTodos().stream()
                .anyMatch(c -> c.getIdCurso() != curso.getIdCurso()
                        && c.getNombreCurso().equalsIgnoreCase(curso.getNombreCurso()));
        if (repetido) {
            throw new IllegalArgumentException("Ya existe un curso con ese nombre.");
        }
        if (curso.getIdCurso() == 0) {
            cursoDAO.insertar(curso);
        } else {
            cursoDAO.actualizar(curso);
        }
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}