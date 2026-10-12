package pe.edu.utp.yachaykard.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.SesionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.Map;

/**
 * Expone a las pantallas los datos del usuario que inició sesión
 * (barra superior y menú por rol) y permite cerrar la sesión.
 */
@Named("sesionBean")
@RequestScoped
public class SesionBean {

    private Map<String, Object> sesion() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    /** @return nombre de usuario de la sesión, o vacío si no hay sesión */
    public String getUsername() {
        Usuario usuario = (Usuario) sesion().get(DatosSesion.USUARIO);
        return usuario != null ? usuario.getUsername() : "";
    }

    /** @return nombre del rol del usuario */
    public String getRol() {
        Object rol = sesion().get(DatosSesion.ROL);
        return rol != null ? rol.toString() : "";
    }

    /** @return nombre de la institución del usuario */
    public String getInstitucion() {
        Object institucion = sesion().get(DatosSesion.INSTITUCION);
        return institucion != null ? institucion.toString() : "";
    }

    public boolean isAdministrador() {
        return DatosSesion.ADMINISTRADOR.equals(getRol());
    }

    public boolean isDocente() {
        return DatosSesion.DOCENTE.equals(getRol());
    }

    public boolean isAuxiliar() {
        return DatosSesion.AUXILIAR.equals(getRol());
    }

    /**
     * Registra la hora de cierre en la tabla sesion y elimina la sesión HTTP.
     *
     * @return redirección a la pantalla de inicio de sesión
     */
    public String cerrarSesion() {
        ExternalContext contexto = FacesContext.getCurrentInstance().getExternalContext();
        Object idSesion = contexto.getSessionMap().get(DatosSesion.ID_SESION);
        if (idSesion != null) {
            try {
                new SesionDAOImpl().cerrarSesion((Integer) idSesion);
            } catch (SQLException e) {
                // Aunque no se registre la hora de cierre, la sesión se cierra igual.
            }
        }
        contexto.invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }
}