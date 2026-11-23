package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.CalificacionDAO;
import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CalificacionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CompetenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.ReporteCierreDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Calificacion;
import pe.edu.utp.yachaykard.modelo.Competencia;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.modelo.ReporteCierre;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.CierrePeriodo;
import pe.edu.utp.yachaykard.servicio.MesesPeriodo;
import pe.edu.utp.yachaykard.util.DatosSesion;
import pe.edu.utp.yachaykard.util.Zona;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Managed Bean de la pantalla Cierre del periodo (RF-30) y su reporte (RF-31).
 * Por cada estudiante muestra los niveles de cada mes, sugiere el nivel final por predominancia
 * y el docente lo confirma o lo cambia. Al cerrar, las calificaciones de esa competencia quedan bloqueadas.
 */
@Named("cierreBean")
@ViewScoped
public class CierreBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String[] MES_CORTO = {"", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Set", "Oct", "Nov", "Dic"};

    private List<CalificacionBean.Opcion> asignaciones = new ArrayList<>();
    private final Map<Integer, AsignacionCurso> mapaAsignaciones = new HashMap<>();
    private List<Competencia> competencias = new ArrayList<>();
    private List<Periodo> periodos = new ArrayList<>();
    private List<Fila> filas = new ArrayList<>();
    private List<Integer> meses = new ArrayList<>();

    private int idAsignacion;
    private int idCompetencia;
    private int idPeriodo;
    private boolean cerrado;

    /**
     * Carga las asignaciones (todas para el administrador; solo las suyas para el docente) y los periodos.
     */
    @PostConstruct
    public void init() {
        try {
            Set<Integer> idsDocente = idsDocenteSesion();
            Map<Integer, String> grados = new HashMap<>();
            for (Grado g : new GradoDAOImpl().listarTodos()) {
                grados.put(g.getIdGrado(), g.getNumeroGrado());
            }
            CursoDAOImpl cursoDAO = new CursoDAOImpl();
            SeccionDAOImpl seccionDAO = new SeccionDAOImpl();
            for (AsignacionCurso a : new AsignacionCursoDAOImpl().listarTodos()) {
                if (idsDocente != null && !idsDocente.contains(a.getIdDocente())) {
                    continue;
                }
                Curso curso = cursoDAO.buscarPorId(a.getIdCurso());
                Seccion seccion = seccionDAO.buscarPorId(a.getIdSeccion());
                String etiqueta = (curso != null ? curso.getNombreCurso() : "Curso") + " – "
                        + (seccion != null ? grados.getOrDefault(seccion.getIdGrado(), "") + " \"" + seccion.getLetra() + "\"" : "");
                asignaciones.add(new CalificacionBean.Opcion(a.getIdAsignacion(), etiqueta));
                mapaAsignaciones.put(a.getIdAsignacion(), a);
            }
            periodos = new PeriodoDAOImpl().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    /**
     * @return ids de docente del usuario si es Docente; null si es Administrador (ve todo)
     * @throws SQLException si ocurre un error en la base de datos
     */
    private Set<Integer> idsDocenteSesion() throws SQLException {
        Map<String, Object> sesion = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (!DatosSesion.DOCENTE.equals(sesion.get(DatosSesion.ROL))) {
            return null;
        }
        Usuario usuario = (Usuario) sesion.get(DatosSesion.USUARIO);
        Set<Integer> ids = new HashSet<>();
        for (Docente d : new DocenteDAOImpl().listarPorUsuario(usuario.getIdUsuario())) {
            ids.add(d.getIdDocente());
        }
        return ids;
    }

    /**
     * Al elegir curso – sección, carga las competencias del curso.
     */
    public void cambiarAsignacion() {
        competencias = new ArrayList<>();
        idCompetencia = 0;
        filas = new ArrayList<>();
        AsignacionCurso a = mapaAsignaciones.get(idAsignacion);
        if (a == null) {
            return;
        }
        try {
            competencias = new CompetenciaDAOImpl().listarPorCurso(a.getIdCurso());
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar las competencias: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de estudiantes con sus niveles de cada mes y el nivel sugerido.
     */
    public void cargar() {
        filas = new ArrayList<>();
        cerrado = false;
        AsignacionCurso a = mapaAsignaciones.get(idAsignacion);
        Periodo periodo = periodos.stream().filter(p -> p.getIdPeriodo() == idPeriodo).findFirst().orElse(null);
        if (a == null || idCompetencia == 0 || periodo == null) {
            return;
        }
        meses = MesesPeriodo.meses(periodo.getFechaInicio(), periodo.getFechaFin());
        try {
            CalificacionDAO calificacionDAO = new CalificacionDAOImpl();
            ReporteCierreDAO reporteDAO = new ReporteCierreDAOImpl();
            Map<Integer, Map<Integer, String>> nivelesPorEstudiante = new HashMap<>();
            for (int mes : meses) {
                for (Calificacion c : calificacionDAO.listarPorSeccion(a.getIdSeccion(), idCompetencia, idPeriodo, mes)) {
                    nivelesPorEstudiante.computeIfAbsent(c.getIdEstudiante(), k -> new HashMap<>())
                            .put(mes, c.getNivelMensual());
                }
            }
            CierrePeriodo cierre = new CierrePeriodo();
            int orden = 1;
            boolean todosCerrados = true;
            for (Estudiante e : new EstudianteDAOImpl().listarPorSeccion(a.getIdSeccion())) {
                Map<Integer, String> porMes = nivelesPorEstudiante.getOrDefault(e.getIdEstudiante(), Map.of());
                List<String> niveles = new ArrayList<>();
                StringBuilder texto = new StringBuilder();
                for (int mes : meses) {
                    String nivel = porMes.get(mes);
                    niveles.add(nivel);
                    texto.append(texto.length() == 0 ? "" : " · ").append(MES_CORTO[mes]).append(": ")
                            .append(nivel == null ? "—" : nivel);
                }
                Fila fila = new Fila(orden++, e, texto.toString(), cierre.sugerirNivel(niveles));
                ReporteCierre guardado = reporteDAO.buscar(e.getIdEstudiante(), idCompetencia, idPeriodo);
                if (guardado != null) {
                    fila.setNivelFinal(guardado.getNivelFinal());
                    fila.setConclusion(guardado.getConclusionDescriptiva());
                    fila.setCerrada(CierrePeriodo.CERRADO.equals(guardado.getEstado()));
                } else {
                    fila.setNivelFinal(fila.getSugerido());
                }
                todosCerrados &= fila.isCerrada();
                filas.add(fila);
            }
            cerrado = !filas.isEmpty() && todosCerrados;
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /**
     * Guarda los niveles finales y cierra la competencia en el periodo.
     */
    public void cerrar() {
        List<ReporteCierre> reportes = new ArrayList<>();
        for (Fila f : filas) {
            ReporteCierre r = new ReporteCierre();
            r.setIdEstudiante(f.getIdEstudiante());
            r.setIdCompetencia(idCompetencia);
            r.setIdPeriodo(idPeriodo);
            r.setNivelFinal(f.getNivelFinal());
            r.setConclusionDescriptiva(f.getConclusion());
            reportes.add(r);
        }
        try {
            List<String> errores = new CierrePeriodo().cerrar(reportes, Zona.hoy());
            if (errores.isEmpty()) {
                mensaje(FacesMessage.SEVERITY_INFO, "Periodo cerrado para esta competencia. "
                        + "Las calificaciones ya no se pueden editar.");
                cargar();
            } else {
                errores.forEach(error -> mensaje(FacesMessage.SEVERITY_ERROR, error));
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cerrar: " + e.getMessage());
        }
    }

    /**
     * @return nombre de la competencia elegida, para el título del reporte
     */
    public String getNombreCompetencia() {
        return competencias.stream().filter(c -> c.getIdCompetencia() == idCompetencia).findFirst()
                .map(Competencia::getDescripcion).orElse("");
    }

    /**
     * @return nombre del curso – sección elegido
     */
    public String getNombreAsignacion() {
        return asignaciones.stream().filter(o -> o.getValor() == idAsignacion).findFirst()
                .map(CalificacionBean.Opcion::getEtiqueta).orElse("");
    }

    /**
     * @return nombre del periodo elegido
     */
    public String getNombrePeriodo() {
        return periodos.stream().filter(p -> p.getIdPeriodo() == idPeriodo).findFirst()
                .map(Periodo::getNombrePeriodo).orElse("");
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<CalificacionBean.Opcion> getAsignaciones() {
        return asignaciones;
    }

    public List<Competencia> getCompetencias() {
        return competencias;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public List<Fila> getFilas() {
        return filas;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public int getIdCompetencia() {
        return idCompetencia;
    }

    public void setIdCompetencia(int idCompetencia) {
        this.idCompetencia = idCompetencia;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public boolean isCerrado() {
        return cerrado;
    }

    /**
     * Una fila del cierre: un estudiante con sus niveles mensuales y su nivel final.
     */
    public static class Fila implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int orden;
        private final int idEstudiante;
        private final String estudiante;
        private final String nivelesMes;
        private final String sugerido;
        private String nivelFinal;
        private String conclusion;
        private boolean cerrada;

        public Fila(int orden, Estudiante e, String nivelesMes, String sugerido) {
            this.orden = orden;
            this.idEstudiante = e.getIdEstudiante();
            this.estudiante = e.getApellidos() + ", " + e.getNombres();
            this.nivelesMes = nivelesMes;
            this.sugerido = sugerido;
        }

        public int getOrden() {
            return orden;
        }

        public int getIdEstudiante() {
            return idEstudiante;
        }

        public String getEstudiante() {
            return estudiante;
        }

        public String getNivelesMes() {
            return nivelesMes;
        }

        public String getSugerido() {
            return sugerido;
        }

        public String getNivelFinal() {
            return nivelFinal;
        }

        public void setNivelFinal(String nivelFinal) {
            this.nivelFinal = nivelFinal;
        }

        public String getConclusion() {
            return conclusion;
        }

        public void setConclusion(String conclusion) {
            this.conclusion = conclusion;
        }

        public boolean isCerrada() {
            return cerrada;
        }

        public void setCerrada(boolean cerrada) {
            this.cerrada = cerrada;
        }

        /**
         * @return true si el docente cambió el nivel sugerido
         */
        public boolean isCambiado() {
            return sugerido != null && nivelFinal != null && !sugerido.equals(nivelFinal);
        }
    }
}