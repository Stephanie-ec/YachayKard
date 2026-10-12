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
 * El administrador edita la lista de tipos de falta según el reglamento de su colegio.
 * Es un mantenimiento simple, por eso llama directo al DAO ("CRUD directo").
 */
@Named("catalogoIncidenciasBean")
@ViewScoped
public class CatalogoIncidenciasBean implements Serializable {

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
        tipo = seleccionado;
    }

    /**
     * Elimina un tipo del catálogo.
     *
     * @param idTipoIncidencia id del tipo a eliminar
     */
    public void eliminar(int idTipoIncidencia) {
        try {
            tipoIncidenciaDAO().eliminar(idTipoIncidencia);
            mensaje(FacesMessage.SEVERITY_INFO, "Tipo de incidencia eliminado.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede eliminar: ya hay incidencias registradas con este tipo.");
        }
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
     * @param severidad tipo de mensaje (información o error)
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