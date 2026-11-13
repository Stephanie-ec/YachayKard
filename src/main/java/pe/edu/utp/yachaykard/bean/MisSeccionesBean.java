package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.GradoDAO;
import pe.edu.utp.yachaykard.dao.MatriculaDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.MatriculaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Managed Bean de la pantalla Mis secciones (RF-14): el docente ve los cursos y
 * secciones que tiene asignados, cuántos estudiantes activos hay en cada una
 * y si es tutor de esa sección.
 */
@Named("misSeccionesBean")
@RequestScoped
public class MisSeccionesBean {

    private static final String ACTIVA = "Activa";

    private List<Fila> filas = new ArrayList<>();

    /** Carga las asignaciones del docente que inició sesión. */
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.USUARIO);
        if (usuario == null) {
            return;
        }
        try {
            CursoDAO cursoDAO = new CursoDAOImpl();
            SeccionDAO seccionDAO = new SeccionDAOImpl();
            GradoDAO gradoDAO = new GradoDAOImpl();
            MatriculaDAO matriculaDAO = new MatriculaDAOImpl();
            for (Docente docente : new DocenteDAOImpl().listarPorUsuario(usuario.getIdUsuario())) {
                for (AsignacionCurso a : new AsignacionCursoDAOImpl().listarPorDocente(docente.getIdDocente())) {
                    Curso curso = cursoDAO.buscarPorId(a.getIdCurso());
                    Seccion seccion = seccionDAO.buscarPorId(a.getIdSeccion());
                    Grado grado = seccion != null ? gradoDAO.buscarPorId(seccion.getIdGrado()) : null;
                    long activos = matriculaDAO.listarPorSeccion(a.getIdSeccion()).stream()
                            .filter(m -> ACTIVA.equals(m.getEstado()))
                            .count();
                    boolean tutor = seccion != null
                            && Objects.equals(seccion.getIdDocenteTutor(), docente.getIdDocente());
                    filas.add(new Fila(
                            curso != null ? curso.getNombreCurso() : "Curso",
                            nombreSeccion(grado, seccion),
                            a.getAnioAcademico(),
                            (int) activos,
                            tutor));
                }
            }
            filas.sort(Comparator.comparing(Fila::getSeccion).thenComparing(Fila::getCurso));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudieron cargar sus secciones: " + e.getMessage(), null));
        }
    }

    private static String nombreSeccion(Grado grado, Seccion seccion) {
        String numero = grado != null ? grado.getNumeroGrado() : "";
        String letra = seccion != null ? String.valueOf(seccion.getLetra()) : "?";
        return numero + " \"" + letra + "\"";
    }

    public List<Fila> getFilas() {
        return filas;
    }

    /** @return cantidad total de estudiantes activos en las secciones del docente */
    public int getTotalEstudiantes() {
        return filas.stream().mapToInt(Fila::getEstudiantes).sum();
    }

    /** Una fila de la tabla: un curso en una sección. */
    public static class Fila {
        private final String curso;
        private final String seccion;
        private final String anio;
        private final int estudiantes;
        private final boolean tutor;

        public Fila(String curso, String seccion, String anio, int estudiantes, boolean tutor) {
            this.curso = curso;
            this.seccion = seccion;
            this.anio = anio;
            this.estudiantes = estudiantes;
            this.tutor = tutor;
        }

        public String getCurso() {
            return curso;
        }

        public String getSeccion() {
            return seccion;
        }

        public String getAnio() {
            return anio;
        }

        public int getEstudiantes() {
            return estudiantes;
        }

        public boolean isTutor() {
            return tutor;
        }
    }
}