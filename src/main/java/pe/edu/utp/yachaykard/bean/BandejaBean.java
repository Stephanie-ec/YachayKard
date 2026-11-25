package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.ApoderadoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.UsuarioDAOImpl;
import pe.edu.utp.yachaykard.modelo.Apoderado;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.Notificacion;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.AvisoWhatsApp;
import pe.edu.utp.yachaykard.servicio.BandejaTutor;
import pe.edu.utp.yachaykard.util.DatosSesion;
import pe.edu.utp.yachaykard.util.Zona;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Managed Bean de la Bandeja del tutor (RF-24, RF-27, RF-28).
 * Muestra las alertas internas del usuario y las incidencias de los estudiantes de su tutoría
 * (el administrador ve todas las secciones). Desde aquí se avisa al apoderado por WhatsApp.
 */
@Named("bandejaBean")
@ViewScoped
public class BandejaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<FilaAlerta> alertas = new ArrayList<>();
    private final List<FilaIncidencia> incidencias = new ArrayList<>();
    private final Map<Integer, Estudiante> estudiantes = new HashMap<>();
    private final Map<Integer, Apoderado> apoderados = new HashMap<>();

    /**
     * Carga las alertas y las incidencias al abrir la pantalla.
     */
    @PostConstruct
    public void init() {
        try {
            new EstudianteDAOImpl().listarTodos().forEach(e -> estudiantes.put(e.getIdEstudiante(), e));
            new ApoderadoDAOImpl().listarTodos().forEach(a -> apoderados.put(a.getIdApoderado(), a));
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la bandeja: " + e.getMessage());
        }
    }

    /**
     * Vuelve a leer las alertas del usuario y las incidencias de su tutoría.
     *
     * @throws SQLException si ocurre un error en la base de datos
     */
    private void cargar() throws SQLException {
        alertas.clear();
        incidencias.clear();
        for (Notificacion n : new BandejaTutor().alertas(usuario().getIdUsuario())) {
            alertas.add(new FilaAlerta(n, nombreEstudiante(n.getIdEstudiante()), celularValido(n.getIdEstudiante())));
        }

        Map<Integer, String> grados = new HashMap<>();
        for (Grado g : new GradoDAOImpl().listarTodos()) {
            grados.put(g.getIdGrado(), g.getNumeroGrado());
        }
        Map<Integer, String> tipos = new HashMap<>();
        for (TipoIncidencia t : new TipoIncidenciaDAOImpl().listarTodos()) {
            tipos.put(t.getIdTipoIncidencia(), t.getNombre());
        }
        Map<Integer, String> usuarios = new HashMap<>();
        for (Usuario u : new UsuarioDAOImpl().listarTodos()) {
            usuarios.put(u.getIdUsuario(), u.getUsername());
        }
        BandejaTutor bandeja = new BandejaTutor();
        Set<String> enviados = bandeja.avisosEnviados();
        IncidenciaDAOImpl incidenciaDAO = new IncidenciaDAOImpl();
        EstudianteDAOImpl estudianteDAO = new EstudianteDAOImpl();
        for (Seccion s : seccionesDelUsuario()) {
            String seccion = grados.getOrDefault(s.getIdGrado(), "") + " \"" + s.getLetra() + "\"";
            for (Estudiante e : estudianteDAO.listarPorSeccion(s.getIdSeccion())) {
                for (Incidencia i : incidenciaDAO.listarPorEstudiante(e.getIdEstudiante())) {
                    incidencias.add(new FilaIncidencia(i, e.getApellidos() + ", " + e.getNombres(), seccion,
                            tipos.getOrDefault(i.getIdTipoIncidencia(), "—"),
                            usuarios.getOrDefault(i.getIdUsuario(), "—"),
                            bandeja.estadoAviso(i, enviados), celularValido(e.getIdEstudiante())));
                }
            }
        }
        incidencias.sort(Comparator.comparing((FilaIncidencia f) -> f.getIncidencia().getFechaHora(),
                Comparator.nullsLast(Comparator.reverseOrder())));
    }

    /**
     * @return todas las secciones si es administrador; si es docente, las secciones donde es tutor
     * @throws SQLException si ocurre un error en la base de datos
     */
    private List<Seccion> seccionesDelUsuario() throws SQLException {
        SeccionDAOImpl seccionDAO = new SeccionDAOImpl();
        if (isAdministrador()) {
            return seccionDAO.listarTodos();
        }
        List<Seccion> secciones = new ArrayList<>();
        for (Docente d : new DocenteDAOImpl().listarPorUsuario(usuario().getIdUsuario())) {
            secciones.addAll(seccionDAO.listarPorTutor(d.getIdDocente()));
        }
        return secciones;
    }

    /**
     * Marca una alerta como leída.
     *
     * @param fila alerta elegida
     */
    public void marcarLeida(FilaAlerta fila) {
        try {
            new BandejaTutor().marcarLeida(fila.getAlerta());
            mensaje(FacesMessage.SEVERITY_INFO, "Alerta marcada como leída.");
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo actualizar la alerta: " + e.getMessage());
        }
    }

    /**
     * Registra el aviso de una alerta y abre WhatsApp en otra pestaña.
     *
     * @param fila alerta de faltas o de riesgo
     */
    public void avisarAlerta(FilaAlerta fila) {
        Estudiante e = estudiantes.get(fila.getAlerta().getIdEstudiante());
        try {
            String enlace = new BandejaTutor().avisarAlerta(fila.getAlerta(), apoderadoDe(e),
                    e.getNombres() + " " + e.getApellidos(), colegio(), usuario().getIdUsuario(), Zona.ahora());
            abrir(enlace);
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (SQLException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo registrar el aviso: " + ex.getMessage());
        }
    }

    /**
     * Registra el aviso de una incidencia Moderada o Grave y abre WhatsApp en otra pestaña.
     *
     * @param fila incidencia elegida
     */
    public void avisarIncidencia(FilaIncidencia fila) {
        Incidencia i = fila.getIncidencia();
        Estudiante e = estudiantes.get(i.getIdEstudiante());
        try {
            String enlace = new BandejaTutor().avisarIncidencia(i, apoderadoDe(e),
                    e.getNombres() + " " + e.getApellidos(), colegio(), fila.getTipo(),
                    usuario().getIdUsuario(), Zona.ahora());
            abrir(enlace);
        } catch (IllegalArgumentException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        } catch (SQLException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo registrar el aviso: " + ex.getMessage());
        }
    }

    /**
     * Vuelve a cargar la bandeja (después de avisar, el estado cambia a Enviado).
     */
    public void actualizar() {
        try {
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la bandeja: " + e.getMessage());
        }
    }

    /**
     * Redirige la pestaña nueva al enlace de WhatsApp.
     *
     * @param enlace enlace wa.me
     */
    private void abrir(String enlace) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(enlace);
        } catch (IOException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo abrir WhatsApp: " + ex.getMessage());
        }
    }

    private Apoderado apoderadoDe(Estudiante e) {
        Apoderado a = e != null ? apoderados.get(e.getIdApoderado()) : null;
        if (a == null) {
            throw new IllegalArgumentException("El estudiante no tiene un apoderado registrado.");
        }
        return a;
    }

    private boolean celularValido(int idEstudiante) {
        Estudiante e = estudiantes.get(idEstudiante);
        Apoderado a = e != null ? apoderados.get(e.getIdApoderado()) : null;
        return a != null && new AvisoWhatsApp().esCelularValido(a.getTelefono());
    }

    private String nombreEstudiante(int idEstudiante) {
        Estudiante e = estudiantes.get(idEstudiante);
        return e == null ? "—" : e.getApellidos() + ", " + e.getNombres();
    }

    /**
     * @param fecha fecha y hora
     * @return fecha en formato dd/MM/yyyy HH:mm
     */
    public String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FECHA_HORA);
    }

    /**
     * @param gravedad Leve, Moderada o Grave
     * @return clase de Bootstrap del distintivo
     */
    public String claseGravedad(String gravedad) {
        if ("Grave".equals(gravedad)) {
            return "bg-danger";
        }
        return "Moderada".equals(gravedad) ? "bg-warning text-dark" : "bg-success";
    }

    /**
     * @param estado estado de la alerta o del aviso
     * @return clase de Bootstrap del distintivo
     */
    public String claseEstado(String estado) {
        if (BandejaTutor.NO_LEIDA.equals(estado) || BandejaTutor.PENDIENTE.equals(estado)) {
            return "bg-warning text-dark";
        }
        return BandejaTutor.SIN_AVISO.equals(estado) ? "bg-light text-dark" : "bg-success";
    }

    private Usuario usuario() {
        return (Usuario) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.USUARIO);
    }

    private String colegio() {
        Object nombre = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.INSTITUCION);
        return nombre != null ? nombre.toString() : "la institución";
    }

    /**
     * @return true si el usuario con sesión es administrador
     */
    public boolean isAdministrador() {
        return DatosSesion.ADMINISTRADOR.equals(FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get(DatosSesion.ROL));
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<FilaAlerta> getAlertas() {
        return alertas;
    }

    public List<FilaIncidencia> getIncidencias() {
        return incidencias;
    }

    /**
     * @return cantidad de alertas sin leer
     */
    public long getAlertasSinLeer() {
        return alertas.stream().filter(f -> BandejaTutor.NO_LEIDA.equals(f.getAlerta().getEstado())).count();
    }

    /**
     * Una fila de alertas: la alerta interna y el nombre del estudiante.
     */
    public static class FilaAlerta implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Notificacion alerta;
        private final String estudiante;
        private final boolean celularValido;

        public FilaAlerta(Notificacion alerta, String estudiante, boolean celularValido) {
            this.alerta = alerta;
            this.estudiante = estudiante;
            this.celularValido = celularValido;
        }

        public Notificacion getAlerta() {
            return alerta;
        }

        public String getEstudiante() {
            return estudiante;
        }

        /**
         * @return true si se puede avisar al apoderado por WhatsApp
         */
        public boolean isAvisable() {
            return celularValido && BandejaTutor.alertaAvisable(alerta);
        }

        /**
         * @return true si el aviso aplica pero el apoderado no tiene celular válido
         */
        public boolean isSinCelular() {
            return !celularValido && BandejaTutor.alertaAvisable(alerta);
        }
    }

    /**
     * Una fila de incidencias: la incidencia con los nombres que se muestran y el estado del aviso.
     */
    public static class FilaIncidencia implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Incidencia incidencia;
        private final String estudiante;
        private final String seccion;
        private final String tipo;
        private final String registradoPor;
        private final String estadoAviso;
        private final boolean celularValido;

        public FilaIncidencia(Incidencia incidencia, String estudiante, String seccion, String tipo,
                              String registradoPor, String estadoAviso, boolean celularValido) {
            this.incidencia = incidencia;
            this.estudiante = estudiante;
            this.seccion = seccion;
            this.tipo = tipo;
            this.registradoPor = registradoPor;
            this.estadoAviso = estadoAviso;
            this.celularValido = celularValido;
        }

        public Incidencia getIncidencia() {
            return incidencia;
        }

        public String getEstudiante() {
            return estudiante;
        }

        public String getSeccion() {
            return seccion;
        }

        public String getTipo() {
            return tipo;
        }

        public String getRegistradoPor() {
            return registradoPor;
        }

        public String getEstadoAviso() {
            return estadoAviso;
        }

        /**
         * @return true si está pendiente de avisar y el apoderado tiene celular válido
         */
        public boolean isAvisable() {
            return BandejaTutor.PENDIENTE.equals(estadoAviso) && celularValido;
        }

        /**
         * @return true si está pendiente pero el apoderado no tiene celular válido
         */
        public boolean isSinCelular() {
            return BandejaTutor.PENDIENTE.equals(estadoAviso) && !celularValido;
        }
    }
}
