package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.servicio.ReglasAsignacion;
import pe.edu.utp.yachaykard.util.Zona;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla Asignación de cursos (RF-08): qué docente dicta qué curso en
 * qué sección. Las reglas están en {@link ReglasAsignacion}.
 */
@Named("asignacionBean")
@ViewScoped
public class AsignacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AsignacionCurso> asignaciones = new ArrayList<>();
    private List<Docente> docentes = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private List<Seccion> secciones = new ArrayList<>();
    private final Map<Integer, String> nombresDocente = new HashMap<>();
    private final Map<Integer, String> nombresCurso = new HashMap<>();
    private final Map<Integer, String> nombresSeccion = new HashMap<>();

    private AsignacionCurso nueva;

    /** Carga docentes, cursos, secciones y las asignaciones registradas. */
    @PostConstruct
    public void init() {
        nueva = nuevaAsignacion();
        try {
            docentes = new DocenteDAOImpl().listarTodos();
            docentes.sort(Comparator.comparing(Docente::getApellidos));
            docentes.forEach(d -> nombresDocente.put(d.getIdDocente(), d.getApellidos() + ", " + d.getNombres()));
            cursos = new CursoDAOImpl().listarTodos();
            cursos.sort(Comparator.comparing(Curso::getNombreCurso));
            cursos.forEach(c -> nombresCurso.put(c.getIdCurso(), c.getNombreCurso()));
            Map<Integer, String> grados = new HashMap<>();
            for (Grado g : new GradoDAOImpl().listarTodos()) {
                grados.put(g.getIdGrado(), g.getNumeroGrado());
            }
            secciones = new SeccionDAOImpl().listarTodos();
            secciones.forEach(s -> nombresSeccion.put(s.getIdSeccion(),
                    grados.getOrDefault(s.getIdGrado(), "") + " \"" + s.getLetra() + "\""));
            secciones.sort(Comparator.comparing(s -> nombresSeccion.get(s.getIdSeccion())));
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los datos: " + e.getMessage());
        }
        cargar();
    }

    private void cargar() {
        try {
            asignaciones = new AsignacionCursoDAOImpl().listarTodos();
            asignaciones.sort(Comparator.comparing((AsignacionCurso a) -> nombreSeccion(a.getIdSeccion()))
                    .thenComparing(a -> nombreCurso(a.getIdCurso())));
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /** Registra la asignación del formulario. */
    public void guardar() {
        try {
            new ReglasAsignacion().registrar(nueva);
            mensaje(FacesMessage.SEVERITY_INFO, "Asignación registrada: " + nombreCurso(nueva.getIdCurso())
                    + " en " + nombreSeccion(nueva.getIdSeccion()) + ".");
            nueva = nuevaAsignacion();
            cargar();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo registrar: " + e.getMessage());
        }
    }

    /**
     * Quita una asignación. Si ya tiene calificaciones, sigue existiendo el registro de notas:
     * solo se libera el curso para otro docente.
     *
     * @param asignacion asignación elegida
     */
    public void eliminar(AsignacionCurso asignacion) {
        try {
            new AsignacionCursoDAOImpl().eliminar(asignacion.getIdAsignacion());
            mensaje(FacesMessage.SEVERITY_INFO, "Asignación eliminada.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar: " + e.getMessage());
        }
    }

    public String nombreDocente(int id) {
        return nombresDocente.getOrDefault(id, "—");
    }

    public String nombreCurso(int id) {
        return nombresCurso.getOrDefault(id, "—");
    }

    public String nombreSeccion(int id) {
        return nombresSeccion.getOrDefault(id, "—");
    }

    private static AsignacionCurso nuevaAsignacion() {
        AsignacionCurso a = new AsignacionCurso();
        a.setAnioAcademico(String.valueOf(Zona.hoy().getYear()));
        return a;
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<AsignacionCurso> getAsignaciones() {
        return asignaciones;
    }

    public List<Docente> getDocentes() {
        return docentes;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public AsignacionCurso getNueva() {
        return nueva;
    }
}