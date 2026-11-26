package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.AsistenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.AuxiliarDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Auxiliar;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.RegistroAsistencia;
import pe.edu.utp.yachaykard.util.DatosSesion;
import pe.edu.utp.yachaykard.util.Zona;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla Asistencia (RF-18) con justificación de un día (RF-19).
 * El auxiliar ve solo las secciones que tiene a su cargo; el administrador, todas.
 */
@Named("asistenciaBean")
@ViewScoped
public class AsistenciaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Seccion> secciones = new ArrayList<>();
    private final Map<Integer, String> nombresSeccion = new HashMap<>();
    private List<Fila> filas = new ArrayList<>();

    private int idSeccion;
    private LocalDate fecha;
    /** Auxiliar con sesión abierta; null si es el administrador. */
    private Integer idAuxiliarSesion;

    /** Carga las secciones a cargo del usuario y pone la fecha de hoy. */
    @PostConstruct
    public void init() {
        fecha = Zona.hoy();
        try {
            Map<String, Object> sesion = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            if (DatosSesion.AUXILIAR.equals(sesion.get(DatosSesion.ROL))) {
                Auxiliar auxiliar = new AuxiliarDAOImpl().buscarPorUsuario(usuarioSesion().getIdUsuario());
                idAuxiliarSesion = auxiliar != null ? auxiliar.getIdAuxiliar() : -1;
            }
            Map<Integer, String> grados = new HashMap<>();
            for (Grado g : new GradoDAOImpl().listarTodos()) {
                grados.put(g.getIdGrado(), g.getNumeroGrado());
            }
            for (Seccion s : new SeccionDAOImpl().listarTodos()) {
                if (idAuxiliarSesion == null || idAuxiliarSesion.equals(s.getIdAuxiliar())) {
                    secciones.add(s);
                    nombresSeccion.put(s.getIdSeccion(), grados.getOrDefault(s.getIdGrado(), "") + " \"" + s.getLetra() + "\"");
                }
            }
            if (secciones.size() == 1) {
                idSeccion = secciones.get(0).getIdSeccion();
                cargar();
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar las secciones: " + e.getMessage());
        }
    }

    /** Carga la lista de estudiantes de la sección con la asistencia ya registrada en la fecha (o Presente). */
    public void cargar() {
        filas = new ArrayList<>();
        if (idSeccion == 0 || fecha == null) {
            return;
        }
        try {
            Map<Integer, Asistencia> existentes = new HashMap<>();
            for (Asistencia a : new AsistenciaDAOImpl().listarPorSeccionYFecha(idSeccion, fecha)) {
                existentes.put(a.getIdEstudiante(), a);
            }
            int orden = 1;
            for (Estudiante e : new EstudianteDAOImpl().listarPorSeccion(idSeccion)) {
                Asistencia a = existentes.get(e.getIdEstudiante());
                if (a == null) {
                    a = new Asistencia();
                    a.setIdEstudiante(e.getIdEstudiante());
                    a.setEstado(RegistroAsistencia.PRESENTE);
                }
                filas.add(new Fila(orden++, e.getApellidos() + ", " + e.getNombres(), a));
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /** Guarda la asistencia de todos los estudiantes de la lista. */
    public void guardar() {
        List<Asistencia> registros = new ArrayList<>();
        filas.forEach(f -> registros.add(f.getAsistencia()));
        try {
            new RegistroAsistencia().guardar(registros, fecha, Zona.hoy(), idAuxiliarQueRegistra());
            mensaje(FacesMessage.SEVERITY_INFO, "Asistencia guardada.");
            cargar();
        } catch (IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la asistencia: " + e.getMessage());
        }
    }

    /**
     * Justifica la falta de una fila con el motivo escrito.
     *
     * @param fila fila con estado Falta ya guardada
     */
    public void justificar(Fila fila) {
        try {
            new RegistroAsistencia().justificar(fila.getAsistencia(), fila.getMotivo(), usuarioSesion().getIdUsuario());
            mensaje(FacesMessage.SEVERITY_INFO, "Falta justificada: " + fila.getEstudiante() + ".");
            fila.setMotivo(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo justificar: " + e.getMessage());
        }
    }

    /** @return auxiliar de la sesión o, si es el administrador, el auxiliar asignado a la sección */
    private int idAuxiliarQueRegistra() {
        if (idAuxiliarSesion != null) {
            return idAuxiliarSesion;
        }
        return secciones.stream().filter(s -> s.getIdSeccion() == idSeccion).findFirst()
                .map(Seccion::getIdAuxiliar).filter(id -> id != null).orElse(0);
    }

    public String nombreSeccion(int id) {
        return nombresSeccion.getOrDefault(id, "—");
    }

    /** @return cantidad de faltas en la lista */
    public long getFaltas() {
        return filas.stream().filter(f -> RegistroAsistencia.FALTA.equals(f.getAsistencia().getEstado())).count();
    }

    private Usuario usuarioSesion() {
        return (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.USUARIO);
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public List<Fila> getFilas() {
        return filas;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /** Una fila de la lista: un estudiante con su registro de asistencia del día. */
    public static class Fila implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int orden;
        private final String estudiante;
        private final Asistencia asistencia;
        private String motivo;

        public Fila(int orden, String estudiante, Asistencia asistencia) {
            this.orden = orden;
            this.estudiante = estudiante;
            this.asistencia = asistencia;
        }

        public int getOrden() {
            return orden;
        }

        public String getEstudiante() {
            return estudiante;
        }

        public Asistencia getAsistencia() {
            return asistencia;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }

        /** @return true si es una falta ya guardada y sin justificar */
        public boolean isJustificable() {
            return asistencia.getIdAsistencia() != 0 && RegistroAsistencia.FALTA.equals(asistencia.getEstado())
                    && asistencia.getIdJustificacion() == null;
        }

        /** @return true si la falta ya está justificada */
        public boolean isJustificada() {
            return asistencia.getIdJustificacion() != null;
        }
    }
}
