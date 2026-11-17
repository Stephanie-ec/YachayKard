package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.AuxiliarDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Auxiliar;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.servicio.ReglasEstructura;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Managed Bean de la pantalla Grados, secciones y cursos (RF-05, RF-06, RF-09).
 * Las reglas están en {@link ReglasEstructura}.
 */
@Named("estructuraBean")
@ViewScoped
public class EstructuraBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String NIVEL = "Secundaria";

    private List<Grado> grados = new ArrayList<>();
    private List<Seccion> secciones = new ArrayList<>();
    private List<Curso> cursos = new ArrayList<>();
    private List<Docente> docentes = new ArrayList<>();
    private List<Auxiliar> auxiliares = new ArrayList<>();

    private Grado grado = nuevoGrado();
    private Seccion seccion = new Seccion();
    /**
     * Letra escrita en el formulario (la sección guarda un char; aquí se usa texto para el campo).
     */
    private String letra;
    private Curso curso = new Curso();

    /**
     * Carga las tres listas y los docentes y auxiliares para elegir tutor y auxiliar.
     */
    @PostConstruct
    public void init() {
        cargar();
        try {
            docentes = new DocenteDAOImpl().listarTodos();
            auxiliares = new AuxiliarDAOImpl().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar docentes y auxiliares: " + e.getMessage());
        }
    }

    private void cargar() {
        try {
            grados = new GradoDAOImpl().listarTodos();
            secciones = new SeccionDAOImpl().listarTodos();
            secciones.sort(Comparator.comparingInt(Seccion::getIdGrado).thenComparing(Seccion::getLetra));
            cursos = new CursoDAOImpl().listarTodos();
            cursos.sort(Comparator.comparing(Curso::getNombreCurso));
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la estructura: " + e.getMessage());
        }
    }

    /**
     * Registra el grado del formulario.
     */
    public void guardarGrado() {
        try {
            reglas().guardarGrado(grado);
            mensaje(FacesMessage.SEVERITY_INFO, "Grado registrado.");
            grado = nuevoGrado();
            cargar();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el grado: " + e.getMessage());
        }
    }

    /**
     * Registra o actualiza la sección del formulario.
     */
    public void guardarSeccion() {
        try {
            boolean nueva = seccion.getIdSeccion() == 0;
            seccion.setLetra(letra == null || letra.isBlank() ? ' ' : letra.trim().charAt(0));
            reglas().guardarSeccion(seccion);
            mensaje(FacesMessage.SEVERITY_INFO, nueva ? "Sección registrada." : "Sección actualizada.");
            seccion = new Seccion();
            letra = null;
            cargar();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la sección: " + e.getMessage());
        }
    }

    /**
     * Registra o actualiza el curso del formulario.
     */
    public void guardarCurso() {
        try {
            boolean nuevo = curso.getIdCurso() == 0;
            reglas().guardarCurso(curso);
            mensaje(FacesMessage.SEVERITY_INFO, nuevo ? "Curso registrado." : "Curso actualizado.");
            curso = new Curso();
            cargar();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el curso: " + e.getMessage());
        }
    }

    /**
     * @param s sección a editar
     */
    public void editarSeccion(Seccion s) {
        seccion = s;
        letra = String.valueOf(s.getLetra());
    }

    /**
     * @param c curso a editar
     */
    public void editarCurso(Curso c) {
        curso = c;
    }

    /**
     * Limpia los formularios de sección y curso.
     */
    public void cancelar() {
        seccion = new Seccion();
        letra = null;
        curso = new Curso();
    }

    /**
     * @param idGrado id del grado
     * @return número de grado para mostrar
     */
    public String nombreGrado(int idGrado) {
        return grados.stream().filter(g -> g.getIdGrado() == idGrado).findFirst()
                .map(Grado::getNumeroGrado).orElse("—");
    }

    /**
     * @param idDocente id del docente tutor (puede ser null)
     * @return nombre del tutor o "Sin tutor"
     */
    public String nombreDocente(Integer idDocente) {
        if (idDocente == null) {
            return "Sin tutor";
        }
        return docentes.stream().filter(d -> d.getIdDocente() == idDocente).findFirst()
                .map(d -> d.getApellidos() + ", " + d.getNombres()).orElse("—");
    }

    /**
     * @param idAuxiliar id del auxiliar (puede ser null)
     * @return nombre del auxiliar o "Sin auxiliar"
     */
    public String nombreAuxiliar(Integer idAuxiliar) {
        if (idAuxiliar == null) {
            return "Sin auxiliar";
        }
        return auxiliares.stream().filter(a -> a.getIdAuxiliar() == idAuxiliar).findFirst()
                .map(a -> a.getApellidos() + ", " + a.getNombres()).orElse("—");
    }

    private static Grado nuevoGrado() {
        Grado g = new Grado();
        g.setNivel(NIVEL);
        return g;
    }

    private ReglasEstructura reglas() {
        return new ReglasEstructura();
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Grado> getGrados() {
        return grados;
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Docente> getDocentes() {
        return docentes;
    }

    public List<Auxiliar> getAuxiliares() {
        return auxiliares;
    }

    public Grado getGrado() {
        return grado;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public Curso getCurso() {
        return curso;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }
}
