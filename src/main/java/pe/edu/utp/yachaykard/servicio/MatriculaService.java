package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.MatriculaDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.dao.impl.MatriculaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Matricula;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Reglas de negocio de la matrícula (RF-12):
 * <ul>
 *     <li>Un estudiante solo puede tener una matrícula "Activa" por año lectivo.</li>
 *     <li>Una sección no puede tener más matrículas activas que su aforo.</li>
 *     <li>Las matrículas no se borran: pasan a "Retirado" o "Trasladado".</li>
 * </ul>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class MatriculaService {

    /** Estado de una matrícula vigente. */
    public static final String ACTIVA = "Activa";
    public static final String RETIRADO = "Retirado";
    public static final String TRASLADADO = "Trasladado";

    private static final Set<String> ESTADOS = Set.of(ACTIVA, RETIRADO, TRASLADADO);
    private static final Pattern ANIO = Pattern.compile("\\d{4}");

    private final MatriculaDAO matriculaDAO;
    private final SeccionDAO seccionDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public MatriculaService() {
        this(new MatriculaDAOImpl(), new SeccionDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param matriculaDAO acceso a matrículas
     * @param seccionDAO   acceso a secciones (para el aforo)
     */
    public MatriculaService(MatriculaDAO matriculaDAO, SeccionDAO seccionDAO) {
        this.matriculaDAO = matriculaDAO;
        this.seccionDAO = seccionDAO;
    }

    /**
     * Registra una matrícula nueva si cumple las reglas.
     *
     * @param matricula datos de la matrícula
     * @throws IllegalArgumentException si faltan datos o son incorrectos
     * @throws IllegalStateException    si ya tiene matrícula activa ese año o la sección está llena
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void registrar(Matricula matricula) throws SQLException {
        validarDatos(matricula);
        if (ACTIVA.equals(matricula.getEstado())) {
            validarActivacion(matricula);
        }
        matriculaDAO.insertar(matricula);
    }

    /**
     * Cambia el estado de una matrícula existente (por ejemplo, Activa a Retirado).
     *
     * @param matricula   matrícula elegida
     * @param nuevoEstado Activa, Retirado o Trasladado
     * @throws IllegalArgumentException si el estado no es válido
     * @throws IllegalStateException    si al reactivarla se rompe una regla
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void cambiarEstado(Matricula matricula, String nuevoEstado) throws SQLException {
        if (nuevoEstado == null || !ESTADOS.contains(nuevoEstado)) {
            throw new IllegalArgumentException("El estado debe ser Activa, Retirado o Trasladado.");
        }
        if (nuevoEstado.equals(matricula.getEstado())) {
            return;
        }
        if (ACTIVA.equals(nuevoEstado)) {
            validarActivacion(matricula);
        }
        matricula.setEstado(nuevoEstado);
        matriculaDAO.actualizar(matricula);
    }

    /**
     * Revisa que la matrícula tenga estudiante, sección, año de 4 dígitos y un estado válido.
     *
     * @param matricula datos a revisar
     * @throws IllegalArgumentException con el primer error encontrado
     */
    public void validarDatos(Matricula matricula) {
        if (matricula == null) {
            throw new IllegalArgumentException("No hay datos de la matrícula.");
        }
        if (matricula.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("Seleccione un estudiante.");
        }
        if (matricula.getIdSeccion() <= 0) {
            throw new IllegalArgumentException("Seleccione una sección.");
        }
        if (matricula.getAnioLectivo() == null || !ANIO.matcher(matricula.getAnioLectivo()).matches()) {
            throw new IllegalArgumentException("El año lectivo debe tener 4 dígitos.");
        }
        if (matricula.getEstado() == null || !ESTADOS.contains(matricula.getEstado())) {
            throw new IllegalArgumentException("El estado debe ser Activa, Retirado o Trasladado.");
        }
        if (matricula.getFechaMatricula() == null) {
            throw new IllegalArgumentException("Ingrese la fecha de matrícula.");
        }
    }

    private void validarActivacion(Matricula matricula) throws SQLException {
        if (matriculaDAO.existeMatriculaActiva(matricula.getIdEstudiante(), matricula.getAnioLectivo())) {
            throw new IllegalStateException(
                    "El estudiante ya tiene una matrícula activa en el año lectivo " + matricula.getAnioLectivo() + ".");
        }
        Seccion seccion = seccionDAO.buscarPorId(matricula.getIdSeccion());
        if (seccion == null) {
            throw new IllegalArgumentException("La sección no existe.");
        }
        long activas = matriculaDAO.listarPorSeccion(matricula.getIdSeccion()).stream()
                .filter(m -> ACTIVA.equals(m.getEstado()))
                .filter(m -> matricula.getAnioLectivo().equals(m.getAnioLectivo()))
                .count();
        if (activas >= seccion.getAforo()) {
            throw new IllegalStateException("La sección ya completó su aforo de " + seccion.getAforo() + " estudiantes.");
        }
    }
}
