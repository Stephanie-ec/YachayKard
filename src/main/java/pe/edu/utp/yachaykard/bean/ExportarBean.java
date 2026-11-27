package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.GradoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.InstitucionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.SeccionDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Institucion;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.modelo.Seccion;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.CodigosSiagie;
import pe.edu.utp.yachaykard.servicio.ExportadorSiagie;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Managed Bean de la pantalla Exportar al SIAGIE (RF-32). El usuario elige la sección y el periodo
 * y descarga el libro RegNotas lleno. El administrador ve todas las secciones; el docente, solo
 * las secciones donde dicta, y su libro trae solo las hojas de sus cursos.
 */
@Named("exportarBean")
@ViewScoped
public class ExportarBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String TIPO_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final List<Seccion> secciones = new ArrayList<>();
    private final Map<Integer, Grado> grados = new HashMap<>();
    private List<Periodo> periodos = new ArrayList<>();
    private Integer idDocente;

    private int idSeccion;
    private int idPeriodo;

    /** Carga las secciones del usuario y los periodos. */
    @PostConstruct
    public void init() {
        try {
            for (Grado g : new GradoDAOImpl().listarTodos()) {
                grados.put(g.getIdGrado(), g);
            }
            Set<Integer> seccionesDelDocente = null;
            if (DatosSesion.DOCENTE.equals(sesion().get(DatosSesion.ROL))) {
                seccionesDelDocente = new HashSet<>();
                for (Docente d : new DocenteDAOImpl().listarPorUsuario(usuario().getIdUsuario())) {
                    idDocente = d.getIdDocente();
                    for (AsignacionCurso a : new AsignacionCursoDAOImpl().listarPorDocente(d.getIdDocente())) {
                        seccionesDelDocente.add(a.getIdSeccion());
                    }
                }
            }
            for (Seccion s : new SeccionDAOImpl().listarTodos()) {
                if (seccionesDelDocente == null || seccionesDelDocente.contains(s.getIdSeccion())) {
                    secciones.add(s);
                }
            }
            periodos = new PeriodoDAOImpl().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    /** Genera el libro RegNotas y lo envía al navegador como descarga. */
    public void descargar() {
        Seccion seccion = secciones.stream().filter(s -> s.getIdSeccion() == idSeccion).findFirst().orElse(null);
        Periodo periodo = periodos.stream().filter(p -> p.getIdPeriodo() == idPeriodo).findFirst().orElse(null);
        if (seccion == null || periodo == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Elija la sección y el periodo.");
            return;
        }
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            Institucion institucion = new InstitucionDAOImpl().buscarPorId(usuario().getIdInstitucion());
            if (institucion == null) {
                mensaje(FacesMessage.SEVERITY_ERROR, "Registre primero los datos de la institución.");
                return;
            }
            ExportadorSiagie.Cabecera cabecera = new ExportadorSiagie.Cabecera(institucion.getCodigoModular(),
                    institucion.getNombre(), grados.get(seccion.getIdGrado()), seccion, periodo);
            byte[] archivo = new ExportadorSiagie().exportar(cabecera, idDocente).getContenido();

            ExternalContext respuesta = contexto.getExternalContext();
            respuesta.responseReset();
            respuesta.setResponseContentType(TIPO_XLSX);
            respuesta.setResponseContentLength(archivo.length);
            respuesta.setResponseHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo(seccion, periodo) + "\"");
            try (OutputStream salida = respuesta.getResponseOutputStream()) {
                salida.write(archivo);
            }
            contexto.responseComplete();
        } catch (IllegalStateException | IllegalArgumentException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (SQLException | IOException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo generar el archivo: " + e.getMessage());
        }
    }

    /**
     * @return por ejemplo RegNotas_1A_T3_2026.xlsx
     */
    private String nombreArchivo(Seccion seccion, Periodo periodo) {
        Grado grado = grados.get(seccion.getIdGrado());
        String numero = grado != null ? grado.getNumeroGrado().replaceAll("\\D", "") : "";
        return "RegNotas_" + numero + seccion.getLetra() + "_" + CodigosSiagie.codigoPeriodo(periodo) + "_"
                + periodo.getFechaInicio().getYear() + ".xlsx";
    }

    /**
     * @param s sección de la lista
     * @return nombre para mostrar, por ejemplo 1° "A"
     */
    public String nombreSeccion(Seccion s) {
        Grado g = grados.get(s.getIdGrado());
        return (g != null ? g.getNumeroGrado() : "") + " \"" + s.getLetra() + "\"";
    }

    private Map<String, Object> sesion() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    private Usuario usuario() {
        return (Usuario) sesion().get(DatosSesion.USUARIO);
    }

    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    /** @return true si el usuario es docente (su libro trae solo sus cursos) */
    public boolean isDocente() {
        return idDocente != null;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }
}