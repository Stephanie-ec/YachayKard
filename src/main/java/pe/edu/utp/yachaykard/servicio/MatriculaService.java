package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.MatriculaDAO;
import pe.edu.utp.yachaykard.dao.impl.MatriculaDAOImpl;
import pe.edu.utp.yachaykard.modelo.Matricula;

import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Reglas de negocio de la matrícula (RF-12):
 * un estudiante solo puede tener una matrícula "Activa" por año lectivo.
 * Las matrículas "Retirado" o "Trasladado" no bloquean una nueva.
 * <p>
 * El DAO se recibe en el constructor para poder simularlo con Mockito en las pruebas.
 */
public class MatriculaService {

    /** Estado de una matrícula vigente. */
    public static final String ACTIVA = "Activa";

    private static final Set<String> ESTADOS = Set.of(ACTIVA, "Retirado", "Trasladado");
    private static final Pattern ANIO = Pattern.compile("\\d{4}");

    private final MatriculaDAO matriculaDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public MatriculaService() {
        this(new MatriculaDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param matriculaDAO acceso a matrículas
     */
    public MatriculaService(MatriculaDAO matriculaDAO) {
        this.matriculaDAO = matriculaDAO;
    }

    /**
     * Registra una matrícula nueva si cumple las reglas.
     *
     * @param matricula datos de la matrícula
     * @throws IllegalArgumentException si faltan datos o son incorrectos
     * @throws IllegalStateException    si el estudiante ya tiene una matrícula activa ese año
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void registrar(Matricula matricula) throws SQLException {
        validarDatos(matricula);
        if (matriculaDAO.existeMatriculaActiva(matricula.getIdEstudiante(), matricula.getAnioLectivo())) {
            throw new IllegalStateException(
                    "El estudiante ya tiene una matrícula activa en el año lectivo " + matricula.getAnioLectivo() + ".");
        }
        matriculaDAO.insertar(matricula);
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
        if (!ESTADOS.contains(matricula.getEstado())) {
            throw new IllegalArgumentException("El estado debe ser Activa, Retirado o Trasladado.");
        }
    }
}
