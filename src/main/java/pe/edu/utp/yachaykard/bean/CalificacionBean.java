package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.AsignacionCursoDAO;
import pe.edu.utp.yachaykard.dao.CalificacionDAO;
import pe.edu.utp.yachaykard.dao.CompetenciaDAO;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.GradoDAO;
import pe.edu.utp.yachaykard.dao.PeriodoDAO;
import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CalificacionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CompetenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.ReporteCierreDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Calificacion;
import pe.edu.utp.yachaykard.modelo.Competencia;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.servicio.GeneradorConclusion;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean de la pantalla "Registro de calificaciones" (calificaciones.xhtml).
 * El docente elige curso-sección, competencia, periodo y mes, y registra un nivel (AD, A, B o C)
 * por estudiante. Cada fila se guarda sola al cambiar un desplegable (AJAX).
 * Usa el servicio GeneradorConclusion para redactar la conclusión descriptiva.
 */
@Named("calificacionBean")
@ViewScoped
public class CalificacionBean implements Serializable {

    private static final String[] NOMBRES_MES = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    private final GeneradorConclusion generador = new GeneradorConclusion();

    private List<Opcion> asignaciones = new ArrayList<>();
    private List<Competencia> competencias = new ArrayList<>();
    private List<Periodo> periodos = new ArrayList<>();
    private List<Opcion> meses = new ArrayList<>();
    private List<Fila> filas = new ArrayList<>();

    /** idAsignacion → asignación, para conocer su curso y su sección. */
    private final Map<Integer, AsignacionCurso> mapaAsignaciones = new HashMap<>();

    private int idAsignacion;
    private int idCompetencia;
    private int idPeriodo;
    private int mes;
    private boolean soloPendientes;
    private boolean periodoCerrado;

    /** Carga las asignaciones (curso – sección) y los periodos al abrir la pantalla. */
    @PostConstruct
    public void init() {
        try {
            CursoDAO cursoDAO = new CursoDAOImpl();
            SeccionDAO seccionDAO = new SeccionDAOImpl();
            GradoDAO gradoDAO = new GradoDAOImpl();
            AsignacionCursoDAO asignacionDAO = new AsignacionCursoDAOImpl();
            for (AsignacionCurso a : asignacionDAO.listarTodos()) {
                Curso curso = cursoDAO.buscarPorId(a.getIdCurso());
                Seccion seccion = seccionDAO.buscarPorId(a.getIdSeccion());
                Grado grado = seccion != null ? gradoDAO.buscarPorId(seccion.getIdGrado()) : null;
                String etiqueta = (curso != null ? curso.getNombreCurso() : "Curso") + " – "
                        + (grado != null ? grado.getNumeroGrado() : "") + " \""
                        + (seccion != null ? seccion.getLetra() : '?') + "\"";
                asignaciones.add(new Opcion(a.getIdAsignacion(), etiqueta));
                mapaAsignaciones.put(a.getIdAsignacion(), a);
            }
            periodos = new PeriodoDAOImpl().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    /** Al elegir curso – sección: carga las competencias del curso y la lista de estudiantes. */
    public void cambiarAsignacion() {
        idCompetencia = 0;
        competencias = new ArrayList<>();
        AsignacionCurso a = mapaAsignaciones.get(idAsignacion);
        if (a != null) {
            try {
                CompetenciaDAO competenciaDAO = new CompetenciaDAOImpl();
                competencias = competenciaDAO.listarPorCurso(a.getIdCurso());
            } catch (SQLException e) {
                mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar las competencias: " + e.getMessage());
            }
        }
        cargarFilas();
    }

    /** Al elegir periodo: arma los meses que caen dentro del periodo y preselecciona el mes actual. */
    public void cambiarPeriodo() {
        meses = new ArrayList<>();
        mes = 0;
        Periodo p = periodoSeleccionado();
        if (p != null && p.getFechaInicio() != null && p.getFechaFin() != null) {
            LocalDate cursor = p.getFechaInicio().withDayOfMonth(1);
            while (!cursor.isAfter(p.getFechaFin())) {
                meses.add(new Opcion(cursor.getMonthValue(), NOMBRES_MES[cursor.getMonthValue()]));
                cursor = cursor.plusMonths(1);
            }
            int mesActual = LocalDate.now().getMonthValue();
            boolean actualDentro = meses.stream().anyMatch(o -> o.getValor() == mesActual);
            mes = actualDentro ? mesActual : (meses.isEmpty() ? 0 : meses.get(0).getValor());
        }
        cargarFilas();
    }

    /** Vuelve a armar la tabla de estudiantes con sus calificaciones del mes elegido. */
    public void cargarFilas() {
        filas = new ArrayList<>();
        AsignacionCurso a = mapaAsignaciones.get(idAsignacion);
        Periodo p = periodoSeleccionado();
        if (a == null || idCompetencia == 0 || p == null || mes == 0) {
            return;
        }
        periodoCerrado = "Cerrado".equals(p.getEstado());
        try {
            EstudianteDAO estudianteDAO = new EstudianteDAOImpl();
            CalificacionDAO calificacionDAO = new CalificacionDAOImpl();
            ReporteCierreDAO reporteDAO = new ReporteCierreDAOImpl();

            Map<Integer, Calificacion> existentes = new HashMap<>();
            for (Calificacion c : calificacionDAO.listarPorSeccion(a.getIdSeccion(), idCompetencia, idPeriodo, mes)) {
                existentes.put(c.getIdEstudiante(), c);
            }

            int orden = 1;
            for (Estudiante e : estudianteDAO.listarPorSeccion(a.getIdSeccion())) {
                Fila fila = new Fila(orden++, e);
                Calificacion c = existentes.get(e.getIdEstudiante());
                if (c != null) {
                    fila.setNivel(c.getNivelMensual());
                    fila.setFortaleza(c.getFortaleza());
                    fila.setDebilidad(c.getDebilidad());
                    fila.setGuardada(true);
                }
                fila.setBloqueada(periodoCerrado
                        || reporteDAO.estaCerrado(e.getIdEstudiante(), idCompetencia, idPeriodo));
                filas.add(fila);
            }
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /**
     * Guarda una fila (se llama por AJAX al cambiar el nivel, la fortaleza o la debilidad).
     * Si el nivel es B o C y falta la debilidad, no guarda y deja la fila pendiente.
     *
     * @param fila fila editada
     */
    public void guardarFila(Fila fila) {
        if (fila.isBloqueada()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Periodo cerrado: las calificaciones no se pueden editar.");
            return;
        }
        if (fila.getNivel() == null || fila.getNivel().isBlank()) {
            return;
        }
        if (generador.requiereDebilidad(fila.getNivel())
                && (fila.getDebilidad() == null || fila.getDebilidad().isBlank())) {
            fila.setGuardada(false);
            fila.setFaltaDebilidad(true);
            mensaje(FacesMessage.SEVERITY_ERROR, "Para el nivel B o C seleccione una debilidad.");
            return;
        }
        fila.setFaltaDebilidad(false);
        String debilidad = generador.requiereDebilidad(fila.getNivel()) ? fila.getDebilidad() : null;

        Calificacion c = new Calificacion();
        c.setIdEstudiante(fila.getEstudiante().getIdEstudiante());
        c.setIdCompetencia(idCompetencia);
        c.setIdPeriodo(idPeriodo);
        c.setMes(mes);
        c.setNivelMensual(fila.getNivel());
        c.setFortaleza(vacioANull(fila.getFortaleza()));
        c.setDebilidad(vacioANull(debilidad));
        c.setConclusionDescriptiva(generador.generar(fila.getNivel(), descripcionCompetencia(),
                fila.getFortaleza(), debilidad));
        try {
            new CalificacionDAOImpl().registrarNota(c);
            fila.setGuardada(true);
        } catch (SQLException e) {
            fila.setGuardada(false);
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la calificación de "
                    + fila.getEstudiante().getApellidos() + ": " + e.getMessage());
        }
    }

    /** @return filas a mostrar según el filtro "Mostrar solo pendientes" */
    public List<Fila> getFilasVisibles() {
        if (!soloPendientes) {
            return filas;
        }
        List<Fila> pendientes = new ArrayList<>();
        for (Fila f : filas) {
            if (!f.isGuardada()) {
                pendientes.add(f);
            }
        }
        return pendientes;
    }

    /** @return cantidad de estudiantes con calificación guardada */
    public int getCalificados() {
        int n = 0;
        for (Fila f : filas) {
            if (f.isGuardada()) {
                n++;
            }
        }
        return n;
    }

    /** @return porcentaje de avance para la barra de progreso */
    public int getPorcentaje() {
        return filas.isEmpty() ? 0 : getCalificados() * 100 / filas.size();
    }

    /**
     * Clase de color del desplegable según el nivel: AD verde, A azul, B ámbar, C rojo.
     *
     * @param nivel nivel de logro
     * @return clases CSS de Bootstrap
     */
    public String claseNivel(String nivel) {
        if (nivel == null) {
            return "form-select form-select-sm";
        }
        switch (nivel) {
            case "AD": return "form-select form-select-sm border-success bg-success-subtle";
            case "A":  return "form-select form-select-sm border-primary bg-primary-subtle";
            case "B":  return "form-select form-select-sm border-warning bg-warning-subtle";
            case "C":  return "form-select form-select-sm border-danger bg-danger-subtle";
            default:   return "form-select form-select-sm";
        }
    }

    /** @return periodo elegido o null */
    private Periodo periodoSeleccionado() {
        for (Periodo p : periodos) {
            if (p.getIdPeriodo() == idPeriodo) {
                return p;
            }
        }
        return null;
    }

    /** @return descripción de la competencia elegida */
    private String descripcionCompetencia() {
        for (Competencia c : competencias) {
            if (c.getIdCompetencia() == idCompetencia) {
                return c.getDescripcion();
            }
        }
        return "la competencia";
    }

    /**
     * @param valor texto
     * @return null si el texto está vacío
     */
    private String vacioANull(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }

    /**
     * Muestra un mensaje en la pantalla.
     *
     * @param severidad tipo de mensaje
     * @param texto     texto a mostrar
     */
    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<String> getFortalezas() {
        return GeneradorConclusion.FORTALEZAS;
    }

    public List<String> getDebilidades() {
        return GeneradorConclusion.DEBILIDADES;
    }

    public List<Opcion> getAsignaciones() {
        return asignaciones;
    }

    public List<Competencia> getCompetencias() {
        return competencias;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public List<Opcion> getMeses() {
        return meses;
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

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public boolean isSoloPendientes() {
        return soloPendientes;
    }

    public void setSoloPendientes(boolean soloPendientes) {
        this.soloPendientes = soloPendientes;
    }

    public boolean isPeriodoCerrado() {
        return periodoCerrado;
    }

    /** Opción de un desplegable (valor numérico + texto visible). */
    public static class Opcion implements Serializable {
        private final int valor;
        private final String etiqueta;

        /**
         * @param valor    valor que se guarda
         * @param etiqueta texto que se muestra
         */
        public Opcion(int valor, String etiqueta) {
            this.valor = valor;
            this.etiqueta = etiqueta;
        }

        public int getValor() {
            return valor;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
    }

    /** Una fila de la tabla: un estudiante con su calificación del mes. */
    public static class Fila implements Serializable {
        private final int orden;
        private final Estudiante estudiante;
        private String nivel;
        private String fortaleza;
        private String debilidad;
        private boolean guardada;
        private boolean bloqueada;
        private boolean faltaDebilidad;

        /**
         * @param orden      número de orden en la lista
         * @param estudiante estudiante de la fila
         */
        public Fila(int orden, Estudiante estudiante) {
            this.orden = orden;
            this.estudiante = estudiante;
        }

        /** @return "APELLIDOS, Nombre" como en la nómina de SIAGIE */
        public String getNombreCompleto() {
            return estudiante.getApellidos().toUpperCase() + ", " + estudiante.getNombres();
        }

        /** @return texto de estado para la columna Estado */
        public String getEstado() {
            if (bloqueada) {
                return "Cerrado";
            }
            if (faltaDebilidad) {
                return "Falta debilidad";
            }
            return guardada ? "✓ Guardado" : "Pendiente";
        }

        public int getOrden() {
            return orden;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public String getNivel() {
            return nivel;
        }

        public void setNivel(String nivel) {
            this.nivel = nivel;
        }

        public String getFortaleza() {
            return fortaleza;
        }

        public void setFortaleza(String fortaleza) {
            this.fortaleza = fortaleza;
        }

        public String getDebilidad() {
            return debilidad;
        }

        public void setDebilidad(String debilidad) {
            this.debilidad = debilidad;
        }

        public boolean isGuardada() {
            return guardada;
        }

        public void setGuardada(boolean guardada) {
            this.guardada = guardada;
        }

        public boolean isBloqueada() {
            return bloqueada;
        }

        public void setBloqueada(boolean bloqueada) {
            this.bloqueada = bloqueada;
        }

        public boolean isFaltaDebilidad() {
            return faltaDebilidad;
        }

        public void setFaltaDebilidad(boolean faltaDebilidad) {
            this.faltaDebilidad = faltaDebilidad;
        }
    }
}
