package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;
import pe.edu.utp.yachaykard.servicio.CatalogoIncidencias;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean de la pantalla "Catálogo de incidencias" (catalogoIncidencias.xhtml).
 * El administrador crea, edita, activa y desactiva los tipos de falta del reglamento
 * de su colegio. Los tipos no se eliminan y el tipo "Otra" siempre queda disponible (RF-22).
 * Es un mantenimiento simple, por eso llama directo al DAO ("CRUD directo").
 */
@Named("catalogoIncidenciasBean")
@ViewScoped
public class CatalogoIncidenciasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TipoIncidencia> tipos = new ArrayList<>();
    private TipoIncidencia tipo = new TipoIncidencia();

    /** Carga el catálogo al abrir la pantalla. */
    @PostConstruct
    public void init() {
        cargar();
    }

    /** Vuelve a leer el catálogo desde la base de datos. */
    public void cargar() {
        try {
            tipos = tipoIncidenciaDAO().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar el catálogo: " + e.getMessage());
        }
    }

    /** Registra un tipo nuevo o actualiza el que se está editando. */
    public void guardar() {
        try {
            boolean nuevo = catalogo().guardar(tipo);
            mensaje(FacesMessage.SEVERITY_INFO, nuevo
                    ? "Tipo de incidencia registrado correctamente."
                    : "Tipo de incidencia actualizado correctamente.");
            tipo = new TipoIncidencia();
            cargar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar: " + e.getMessage());
        }
    }

    /**
     * Carga un tipo en el formulario para editarlo.
     *
     * @param seleccionado tipo elegido en la tabla
     */
    public void editar(TipoIncidencia seleccionado) {
        if (esBloqueado(seleccionado)) {
            mensaje(FacesMessage.SEVERITY_WARN, "El tipo \"Otra\" no se puede modificar.");
            return;
        }
        tipo = seleccionado;
    }

    /**
     * Activa o desactiva un tipo. Un tipo desactivado deja de ofrecerse
     * al registrar incidencias, pero las incidencias ya registradas se conservan.
     *
     * @param seleccionado tipo elegido en la tabla
     */
    public void cambiarEstado(TipoIncidencia seleccionado) {
        try {
            boolean activar = catalogo().cambiarEstado(seleccionado);
            if (tipo.getIdTipoIncidencia() == seleccionado.getIdTipoIncidencia()) {
                tipo = new TipoIncidencia();
            }
            mensaje(FacesMessage.SEVERITY_INFO,
                    "Tipo de incidencia " + (activar ? "activado" : "desactivado") + " correctamente.");
            cargar();
        } catch (IllegalStateException e) {
            mensaje(FacesMessage.SEVERITY_WARN, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cambiar el estado: " + e.getMessage());
        }
    }

    /**
     * @param t tipo de la fila
     * @return true si es el tipo "Otra", que no se edita ni se desactiva
     */
    public boolean esBloqueado(TipoIncidencia t) {
        return catalogo().esBloqueado(t);
    }

    /**
     * Clase de Bootstrap del distintivo de gravedad.
     *
     * @param gravedad Grave, Moderada o Leve
     * @return clase CSS del distintivo
     */
    public String claseGravedad(String gravedad) {
        if ("Grave".equals(gravedad)) {
            return "bg-danger";
        }
        if ("Moderada".equals(gravedad)) {
            return "bg-warning text-dark";
        }
        return "bg-success";
    }

    /** Limpia el formulario. */
    public void cancelar() {
        tipo = new TipoIncidencia();
    }

    /** @return DAO del catálogo (se crea en cada uso porque no guarda estado) */
    private TipoIncidenciaDAO tipoIncidenciaDAO() {
        return new TipoIncidenciaDAOImpl();
    }

    /** @return reglas del catálogo (se crean en cada uso porque no guardan estado) */
    private CatalogoIncidencias catalogo() {
        return new CatalogoIncidencias();
    }

    /**
     * Muestra un mensaje en la pantalla.
     *
     * @param severidad tipo de mensaje (información, advertencia o error)
     * @param texto     texto a mostrar
     */
    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<TipoIncidencia> getTipos() {
        return tipos;
    }

    public TipoIncidencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoIncidencia tipo) {
        this.tipo = tipo;
    }
}
