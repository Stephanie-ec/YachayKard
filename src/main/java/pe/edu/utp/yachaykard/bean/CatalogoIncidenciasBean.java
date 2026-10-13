package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.TipoIncidenciaDAO;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

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

    /** Tipo que no se puede editar ni desactivar. */
    private static final String TIPO_OTRA = "Otra";

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
        tipo.setNombre(tipo.getNombre().trim());
        try {
            if (tipoIncidenciaDAO().existeNombre(tipo.getNombre(), tipo.getIdTipoIncidencia())) {
                mensaje(FacesMessage.SEVERITY_ERROR, "Ya existe un tipo con este nombre.");
                return;
            }
            if (tipo.getIdTipoIncidencia() == 0) {
                tipoIncidenciaDAO().registrarTipo(tipo);
                mensaje(FacesMessage.SEVERITY_INFO, "Tipo de incidencia registrado correctamente.");
            } else {
                tipoIncidenciaDAO().actualizar(tipo);
                mensaje(FacesMessage.SEVERITY_INFO, "Tipo de incidencia actualizado correctamente.");
            }
            tipo = new TipoIncidencia();
            cargar();
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
        if (esBloqueado(seleccionado)) {
            mensaje(FacesMessage.SEVERITY_WARN, "El tipo \"Otra\" siempre debe estar disponible.");
            return;
        }
        boolean activar = !seleccionado.isActivo();
        try {
            tipoIncidenciaDAO().cambiarEstado(seleccionado.getIdTipoIncidencia(), activar);
            if (tipo.getIdTipoIncidencia() == seleccionado.getIdTipoIncidencia()) {
                tipo = new TipoIncidencia();
            }
            mensaje(FacesMessage.SEVERITY_INFO,
                    "Tipo de incidencia " + (activar ? "activado" : "desactivado") + " correctamente.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cambiar el estado: " + e.getMessage());
        }
    }

    /**
     * @param t tipo de la fila
     * @return true si es el tipo "Otra", que no se edita ni se desactiva
     */
    public boolean esBloqueado(TipoIncidencia t) {
        return TIPO_OTRA.equalsIgnoreCase(t.getNombre());
    }

    /** Limpia el formulario. */
    public void cancelar() {
        tipo = new TipoIncidencia();
    }

    /** @return DAO del catálogo (se crea en cada uso porque no guarda estado) */
    private TipoIncidenciaDAO tipoIncidenciaDAO() {
        return new TipoIncidenciaDAOImpl();
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
