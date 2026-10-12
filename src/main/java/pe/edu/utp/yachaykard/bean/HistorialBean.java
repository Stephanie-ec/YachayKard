package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.AccesoEstudiantes;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla "Historial del estudiante" (historial.xhtml).
 * Solo arma la lista de estudiantes que el usuario puede consultar;
 * los datos del historial y del riesgo se piden a los servicios REST
 * desde el navegador.
 */
@Named("historialBean")
@RequestScoped
public class HistorialBean {

    private List<Estudiante> estudiantes = new ArrayList<>();

    /**
     * Carga los estudiantes permitidos según el rol del usuario.
     */
    @PostConstruct
    public void init() {
        Map<String, Object> sesion = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Usuario usuario = (Usuario) sesion.get(DatosSesion.USUARIO);
        if (usuario == null) {
            return;
        }
        try {
            estudiantes = new AccesoEstudiantes().permitidos(usuario, (String) sesion.get(DatosSesion.ROL));
            estudiantes.sort(Comparator.comparing(Estudiante::getApellidos).thenComparing(Estudiante::getNombres));
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo cargar la lista de estudiantes: " + e.getMessage(), null));
        }
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }
}