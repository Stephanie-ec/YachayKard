package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.ApoderadoDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.impl.ApoderadoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.modelo.Apoderado;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.servicio.ValidadorApoderado;
import pe.edu.utp.yachaykard.servicio.ValidadorEstudiante;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean de la pantalla "Gestión de estudiantes" (estudiantes.xhtml).
 * Es un mantenimiento simple, por eso llama directo a los DAO ("CRUD directo").
 */
@Named("estudianteBean")
@ViewScoped
public class EstudianteBean implements Serializable {

    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<Apoderado> apoderados = new ArrayList<>();
    private Estudiante estudiante = new Estudiante();
    private Apoderado nuevoApoderado = new Apoderado();
    private boolean mostrarApoderado;

    /**
     * Carga la lista de estudiantes y de apoderados al abrir la pantalla.
     */
    @PostConstruct
    public void init() {
        cargar();
    }

    /**
     * Vuelve a leer estudiantes y apoderados desde la base de datos.
     */
    public void cargar() {
        try {
            estudiantes = estudianteDAO().listarTodos();
            apoderados = apoderadoDAO().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /**
     * Registra un estudiante nuevo o actualiza el que se está editando.
     */
    public void guardar() {
        List<String> errores = new ValidadorEstudiante().validar(estudiante);
        if (!errores.isEmpty()) {
            errores.forEach(error -> mensaje(FacesMessage.SEVERITY_ERROR, error));
            return;
        }
        boolean dniRepetido = estudiantes.stream()
                .anyMatch(e -> e.getDni().equals(estudiante.getDni())
                        && e.getIdEstudiante() != estudiante.getIdEstudiante());
        if (dniRepetido) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Ya existe un estudiante con ese DNI.");
            return;
        }
        try {
            if (estudiante.getIdEstudiante() == 0) {
                estudianteDAO().registrar(estudiante);
                mensaje(FacesMessage.SEVERITY_INFO, "Estudiante registrado correctamente.");
            } else {
                estudianteDAO().actualizar(estudiante);
                mensaje(FacesMessage.SEVERITY_INFO, "Estudiante actualizado correctamente.");
            }
            estudiante = new Estudiante();
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el estudiante: " + e.getMessage());
        }
    }

    /**
     * Muestra u oculta el formulario de nuevo apoderado.
     */
    public void alternarApoderado() {
        mostrarApoderado = !mostrarApoderado;
        nuevoApoderado = new Apoderado();
    }

    /**
     * Registra un apoderado nuevo (RF-11) y lo deja elegido para el estudiante del formulario.
     */
    public void guardarApoderado() {
        nuevoApoderado.setNombres(nuevoApoderado.getNombres() == null ? null : nuevoApoderado.getNombres().trim());
        List<String> errores = new ValidadorApoderado().validar(nuevoApoderado, apoderados);
        if (!errores.isEmpty()) {
            errores.forEach(error -> mensaje(FacesMessage.SEVERITY_ERROR, error));
            return;
        }
        try {
            if (nuevoApoderado.getTelefono() != null && nuevoApoderado.getTelefono().isBlank()) {
                nuevoApoderado.setTelefono(null);
            }
            apoderadoDAO().insertar(nuevoApoderado);
            apoderados = apoderadoDAO().listarTodos();
            String dni = nuevoApoderado.getDni();
            apoderados.stream().filter(a -> dni.equals(a.getDni())).findFirst()
                    .ifPresent(a -> estudiante.setIdApoderado(a.getIdApoderado()));
            mensaje(FacesMessage.SEVERITY_INFO, "Apoderado registrado y elegido en el formulario del estudiante.");
            nuevoApoderado = new Apoderado();
            mostrarApoderado = false;
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo registrar el apoderado: " + e.getMessage());
        }
    }

    /**
     * Carga un estudiante en el formulario para editarlo.
     *
     * @param seleccionado estudiante elegido en la tabla
     */
    public void editar(Estudiante seleccionado) {
        estudiante = seleccionado;
    }

    /**
     * Elimina un estudiante.
     *
     * @param idEstudiante id del estudiante a eliminar
     */
    public void eliminar(int idEstudiante) {
        try {
            estudianteDAO().eliminar(idEstudiante);
            mensaje(FacesMessage.SEVERITY_INFO, "Estudiante eliminado.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede eliminar: el estudiante tiene matrículas o registros asociados.");
        }
    }

    /**
     * Limpia el formulario.
     */
    public void cancelar() {
        estudiante = new Estudiante();
    }

    /**
     * Devuelve el nombre del apoderado para mostrarlo en la tabla.
     *
     * @param idApoderado id del apoderado
     * @return nombre del apoderado o "-" si no se encuentra
     */
    public String nombreApoderado(int idApoderado) {
        for (Apoderado a : apoderados) {
            if (a.getIdApoderado() == idApoderado) {
                return a.getNombres();
            }
        }
        return "-";
    }

    /**
     * @return DAO de estudiantes (se crea en cada uso porque no guarda estado)
     */
    private EstudianteDAO estudianteDAO() {
        return new EstudianteDAOImpl();
    }

    /**
     * @return DAO de apoderados
     */
    private ApoderadoDAO apoderadoDAO() {
        return new ApoderadoDAOImpl();
    }

    /**
     * Muestra un mensaje en la pantalla.
     *
     * @param severidad tipo de mensaje (información o error)
     * @param texto     texto a mostrar
     */
    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<Apoderado> getApoderados() {
        return apoderados;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Apoderado getNuevoApoderado() {
        return nuevoApoderado;
    }

    public boolean isMostrarApoderado() {
        return mostrarApoderado;
    }
}
