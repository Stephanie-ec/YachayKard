package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.MatriculaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Matricula;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.servicio.MatriculaService;
import pe.edu.utp.yachaykard.util.Zona;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla Matrículas (RF-12): matricular a un estudiante en una sección
 * y cambiar el estado de la matrícula (retiro o traslado). Las reglas están en {@link MatriculaService}.
 */
@Named("matriculaBean")
@ViewScoped
public class MatriculaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Seccion> secciones = new ArrayList<>();
    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Matricula> matriculas = new ArrayList<>();
    private final Map<Integer, String> nombresGrado = new HashMap<>();
    private final Map<Integer, String> nombresEstudiante = new HashMap<>();

    private String anioLectivo;
    private int idSeccion;
    private Matricula nueva;

    /** Carga secciones y estudiantes, y prepara una matrícula nueva para el año actual. */
    @PostConstruct
    public void init() {
        anioLectivo = String.valueOf(Zona.hoy().getYear());
        nueva = nuevaMatricula();
        try {
            for (Grado g : new GradoDAOImpl().listarTodos()) {
                nombresGrado.put(g.getIdGrado(), g.getNumeroGrado());
            }
            secciones = new SeccionDAOImpl().listarTodos();
            secciones.sort(Comparator.comparing(this::nombreSeccion));
            estudiantes = new EstudianteDAOImpl().listarTodos();
            estudiantes.sort(Comparator.comparing(Estudiante::getApellidos));
            for (Estudiante e : estudiantes) {
                nombresEstudiante.put(e.getIdEstudiante(), e.getApellidos() + ", " + e.getNombres());
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    /** Carga las matrículas de la sección y el año elegidos. */
    public void cargarMatriculas() {
        matriculas = new ArrayList<>();
        if (idSeccion == 0) {
            return;
        }
        try {
            for (Matricula m : new MatriculaDAOImpl().listarPorSeccion(idSeccion)) {
                if (anioLectivo.equals(m.getAnioLectivo())) {
                    matriculas.add(m);
                }
            }
            matriculas.sort(Comparator.comparing(m -> nombreEstudiante(m.getIdEstudiante())));
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar las matrículas: " + e.getMessage());
        }
    }

    /** Registra la matrícula del formulario. */
    public void matricular() {
        nueva.setAnioLectivo(anioLectivo);
        try {
            new MatriculaService().registrar(nueva);
            mensaje(FacesMessage.SEVERITY_INFO, "Matrícula registrada: " + nombreEstudiante(nueva.getIdEstudiante())
                    + " en " + nombreSeccion(nueva.getIdSeccion()) + ".");
            idSeccion = nueva.getIdSeccion();
            nueva = nuevaMatricula();
            cargarMatriculas();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo registrar la matrícula: " + e.getMessage());
        }
    }

    /**
     * Cambia el estado de una matrícula (Retirado o Trasladado).
     *
     * @param matricula   matrícula elegida
     * @param nuevoEstado estado nuevo
     */
    public void cambiarEstado(Matricula matricula, String nuevoEstado) {
        try {
            new MatriculaService().cambiarEstado(matricula, nuevoEstado);
            mensaje(FacesMessage.SEVERITY_INFO, "La matrícula quedó en estado " + nuevoEstado + ".");
            cargarMatriculas();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cambiar el estado: " + e.getMessage());
        }
    }

    /**
     * @param seccion sección
     * @return nombre para mostrar, por ejemplo 1° "A"
     */
    public String nombreSeccion(Seccion seccion) {
        return nombresGrado.getOrDefault(seccion.getIdGrado(), "") + " \"" + seccion.getLetra() + "\"";
    }

    /**
     * @param id id de la sección
     * @return nombre para mostrar
     */
    public String nombreSeccion(int id) {
        return secciones.stream().filter(s -> s.getIdSeccion() == id).findFirst()
                .map(this::nombreSeccion).orElse("—");
    }

    /**
     * @param id id del estudiante
     * @return apellidos y nombres
     */
    public String nombreEstudiante(int id) {
        return nombresEstudiante.getOrDefault(id, "—");
    }

    /** @return cantidad de matrículas activas de la sección elegida */
    public long getActivas() {
        return matriculas.stream().filter(m -> MatriculaService.ACTIVA.equals(m.getEstado())).count();
    }

    /** @return aforo de la sección elegida, o 0 si no hay sección */
    public int getAforo() {
        return secciones.stream().filter(s -> s.getIdSeccion() == idSeccion).findFirst()
                .map(Seccion::getAforo).orElse(0);
    }

    private Matricula nuevaMatricula() {
        Matricula m = new Matricula();
        m.setFechaMatricula(Zona.hoy());
        m.setEstado(MatriculaService.ACTIVA);
        return m;
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public String getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(String anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public Matricula getNueva() {
        return nueva;
    }
}
