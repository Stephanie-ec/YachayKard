package pe.edu.utp.yachaykard.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.UsuarioDAO;
import pe.edu.utp.yachaykard.dao.impl.InstitucionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.RolDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SesionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.UsuarioDAOImpl;
import pe.edu.utp.yachaykard.modelo.Institucion;
import pe.edu.utp.yachaykard.modelo.Rol;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.Map;

/**
 * Managed Bean de la pantalla de inicio de sesión (login.xhtml).
 * Valida usuario y contraseña, registra la sesión en la tabla sesion
 * y guarda en la sesión HTTP los datos que usan el menú y el filtro de acceso.
 */
@Named("loginBean")
@RequestScoped
public class LoginBean {

    private String username;
    private String password;

    /**
     * Inicia sesión con las credenciales ingresadas.
     *
     * @return redirección al panel principal, o null si los datos no son válidos
     */
    public String ingresar() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
            if (!usuarioDAO.autenticar(username, password)) {
                mensaje("Usuario o contraseña incorrectos.");
                return null;
            }

            Usuario usuario = usuarioDAO.buscarPorUsername(username);
            usuario.setPassword(null);
            Rol rol = new RolDAOImpl().buscarPorId(usuario.getIdRol());
            Institucion institucion = new InstitucionDAOImpl().buscarPorId(usuario.getIdInstitucion());
            int idSesion = new SesionDAOImpl().iniciarSesion(usuario.getIdUsuario());

            Map<String, Object> sesion = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            sesion.put(DatosSesion.USUARIO, usuario);
            sesion.put(DatosSesion.ROL, rol != null ? rol.getNombreRol() : "");
            sesion.put(DatosSesion.INSTITUCION, institucion != null ? institucion.getNombre() : "");
            sesion.put(DatosSesion.ID_SESION, idSesion);

            return "/index.xhtml?faces-redirect=true";
        } catch (SQLException e) {
            mensaje("No se pudo conectar con la base de datos.");
            return null;
        }
    }

    private void mensaje(String texto) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, texto, null));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}