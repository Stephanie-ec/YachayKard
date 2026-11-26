package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.ReglasIncidencia;
import pe.edu.utp.yachaykard.util.DatosSesion;
import pe.edu.utp.yachaykard.util.Zona;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla Registro de incidencias (RF-23). La pueden usar el administrador,
 * los docentes y los auxiliares. Las reglas están en {@link ReglasIncidencia}.
 */
@Named("incidenciaBean")
@ViewScoped
public class IncidenciaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int RECIENTES = 20;
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private List<Estudiante> estudiantes = new ArrayList<>();
    private List<TipoIncidencia> tipos = new ArrayList<>();
    private List<Incidencia> recientes = new ArrayList<>();
    private final Map<Integer, String> nombresEstudiante = new HashMap<>();
    private final Map<Integer, String> nombresTipo = new HashMap<>();

    private Incidencia incidencia = new Incidencia();

    /** Carga estudiantes, tipos activos y las incidencias recientes. */
    @PostConstruct
    public void init() {
        try {
            estudiantes = new EstudianteDAOImpl().listarTodos();
            estudiantes.sort(Comparator.comparing(Estudiante::getApellidos));
            estudiantes.forEach(e -> nombresEstudiante.put(e.getIdEstudiante(), e.getApellidos() + ", " + e.getNombres()));
            tipos = new TipoIncidenciaDAOImpl().listarActivos();
            for (TipoIncidencia t : new TipoIncidenciaDAOImpl().listarTodos()) {
                nombresTipo.put(t.getIdTipoIncidencia(), t.getNombre());
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los datos: " + e.getMessage());
        }
        cargarRecientes();
    }

    private void cargarRecientes() {
        try {
            recientes = new IncidenciaDAOImpl().listarRecientes(RECIENTES);
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar las incidencias: " + e.getMessage());
        }
    }

    /** Al elegir el tipo, propone su gravedad predeterminada (se puede cambiar). */
    public void cambiarTipo() {
        tipos.stream().filter(t -> t.getIdTipoIncidencia() == incidencia.getIdTipoIncidencia()).findFirst()
                .ifPresent(t -> incidencia.setGravedad(t.getGravedadSugerida()));
    }

    /** Registra la incidencia del formulario. */
    public void guardar() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.USUARIO);
        incidencia.setIdUsuario(usuario.getIdUsuario());
        try {
            new ReglasIncidencia().registrar(incidencia, Zona.ahora());
            mensaje(FacesMessage.SEVERITY_INFO, "Incidencia registrada"
                    + (incidencia.isRequiereSiseve() ? " y marcada para reportar en el SíseVe." : "."));
            incidencia = new Incidencia();
            cargarRecientes();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo registrar la incidencia: " + e.getMessage());
        }
    }

    /** Limpia el formulario. */
    public void cancelar() {
        incidencia = new Incidencia();
    }

    public String nombreEstudiante(int id) {
        return nombresEstudiante.getOrDefault(id, "—");
    }

    public String nombreTipo(int id) {
        return nombresTipo.getOrDefault(id, "—");
    }

    /**
     * @param i incidencia de la tabla
     * @return fecha y hora en formato dd/MM/yyyy HH:mm
     */
    public String fechaHora(Incidencia i) {
        return i.getFechaHora() == null ? "" : i.getFechaHora().format(FECHA_HORA);
    }

    /**
     * @param gravedad Leve, Moderada o Grave
     * @return clase de Bootstrap del distintivo
     */
    public String claseGravedad(String gravedad) {
        if (ReglasIncidencia.GRAVE.equals(gravedad)) {
            return "bg-danger";
        }
        return "Moderada".equals(gravedad) ? "bg-warning text-dark" : "bg-success";
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<TipoIncidencia> getTipos() {
        return tipos;
    }

    public List<Incidencia> getRecientes() {
        return recientes;
    }

    public List<String> getAcciones() {
        return ReglasIncidencia.ACCIONES;
    }

    public Incidencia getIncidencia() {
        return incidencia;
    }
}
