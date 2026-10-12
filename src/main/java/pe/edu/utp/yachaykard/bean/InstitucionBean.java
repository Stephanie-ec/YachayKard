package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.InstitucionDAO;
import pe.edu.utp.yachaykard.dao.impl.InstitucionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Institucion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla "Datos de la institución" (institucion.xhtml).
 * El administrador consulta y actualiza los datos de su institución.
 */
@Named("institucionBean")
@ViewScoped
public class InstitucionBean implements Serializable {

    /** Niveles educativos que se pueden elegir. */
    private static final List<String> NIVELES = List.of("Inicial", "Primaria", "Secundaria");

    private Institucion institucion = new Institucion();

    /**
     * Carga la institución del usuario que inició sesión.
     */
    @PostConstruct
    public void init() {
        Usuario usuario = (Usuario) sesion().get(DatosSesion.USUARIO);
        if (usuario == null) {
            return;
        }
        try {
            Institucion encontrada = institucionDAO().buscarPorId(usuario.getIdInstitucion());
            if (encontrada != null) {
                institucion = encontrada;
            } else {
                mensaje(FacesMessage.SEVERITY_ERROR, "No se encontró la institución del usuario.");
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la institución: " + e.getMessage());
        }
    }

    /**
     * Guarda los cambios y actualiza el nombre que se muestra en la barra superior.
     */
    public void guardar() {
        if (!institucion.getCodigoModular().matches("\\d{7}")) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El código modular debe tener 7 dígitos.");
            return;
        }
        try {
            institucionDAO().actualizar(institucion);
            sesion().put(DatosSesion.INSTITUCION, institucion.getNombre());
            mensaje(FacesMessage.SEVERITY_INFO, "Datos de la institución actualizados.");
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar: " + e.getMessage());
        }
    }

    private InstitucionDAO institucionDAO() {
        return new InstitucionDAOImpl();
    }

    private Map<String, Object> sesion() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    private void mensaje(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    public List<String> getNiveles() {
        return NIVELES;
    }
}