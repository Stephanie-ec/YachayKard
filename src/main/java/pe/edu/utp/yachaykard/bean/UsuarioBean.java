package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.RolDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.UsuarioDAOImpl;
import pe.edu.utp.yachaykard.modelo.Rol;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.GestionUsuarios;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla Usuarios (RF-02): crear, editar y activar o desactivar
 * las cuentas del sistema. Las reglas están en {@link GestionUsuarios}.
 */
@Named("usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Rol> roles = new ArrayList<>();
    private final Map<Integer, String> nombresRol = new HashMap<>();

    private Usuario usuario = new Usuario();
    private String clave;
    private GestionUsuarios.DatosPersona persona = new GestionUsuarios.DatosPersona();

    /** Carga los roles y la lista de usuarios. */
    @PostConstruct
    public void init() {
        try {
            roles = new RolDAOImpl().listarTodos();
            roles.forEach(r -> nombresRol.put(r.getIdRol(), r.getNombreRol()));
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los roles: " + e.getMessage());
        }
        cargar();
    }

    private void cargar() {
        try {
            usuarios = new UsuarioDAOImpl().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /** Registra un usuario nuevo o guarda los cambios del que se está editando. */
    public void guardar() {
        try {
            if (isEditando()) {
                servicio().actualizar(usuario, clave);
                mensaje(FacesMessage.SEVERITY_INFO, "Usuario actualizado correctamente.");
            } else {
                usuario.setIdInstitucion(usuarioSesion().getIdInstitucion());
                servicio().registrar(usuario, getNombreRolElegido(), clave, persona);
                mensaje(FacesMessage.SEVERITY_INFO, "Usuario registrado correctamente.");
            }
            cancelar();
            cargar();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar: " + e.getMessage());
        }
    }

    /**
     * Carga un usuario en el formulario. El rol no se cambia al editar.
     *
     * @param seleccionado usuario elegido en la tabla
     */
    public void editar(Usuario seleccionado) {
        usuario = seleccionado;
        clave = null;
        persona = new GestionUsuarios.DatosPersona();
    }

    /**
     * Activa o desactiva un usuario.
     *
     * @param seleccionado usuario elegido en la tabla
     */
    public void cambiarEstado(Usuario seleccionado) {
        try {
            boolean activo = servicio().cambiarEstado(seleccionado, usuarioSesion().getIdUsuario());
            mensaje(FacesMessage.SEVERITY_INFO, activo ? "Usuario activado." : "Usuario desactivado: ya no podrá iniciar sesión.");
            cargar();
        } catch (IllegalStateException e) {
            mensaje(FacesMessage.SEVERITY_WARN, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cambiar el estado: " + e.getMessage());
        }
    }

    /** Limpia el formulario. */
    public void cancelar() {
        usuario = new Usuario();
        clave = null;
        persona = new GestionUsuarios.DatosPersona();
    }

    /**
     * @param idRol id del rol
     * @return nombre del rol para mostrar en la tabla
     */
    public String nombreRol(int idRol) {
        return nombresRol.getOrDefault(idRol, "—");
    }

    /** @return nombre del rol elegido en el formulario */
    public String getNombreRolElegido() {
        return nombresRol.get(usuario.getIdRol());
    }

    /** @return true si el rol elegido pide datos personales (Docente o Auxiliar) y el usuario es nuevo */
    public boolean isPidePersona() {
        return !isEditando() && servicio().requierePersona(getNombreRolElegido());
    }

    /** @return true si se está editando un usuario existente */
    public boolean isEditando() {
        return usuario.getIdUsuario() != 0;
    }

    /**
     * @param u usuario de la tabla
     * @return true si es el usuario que tiene la sesión abierta
     */
    public boolean esUsuarioSesion(Usuario u) {
        return u.getIdUsuario() == usuarioSesion().getIdUsuario();
    }

    private Usuario usuarioSesion() {
        return (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.USUARIO);
    }

    private GestionUsuarios servicio() {
        return new GestionUsuarios();
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public GestionUsuarios.DatosPersona getPersona() {
        return persona;
    }
}