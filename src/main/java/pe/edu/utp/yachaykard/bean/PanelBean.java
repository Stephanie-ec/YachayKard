package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.PrediccionRiesgoDAO;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.MatriculaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PrediccionRiesgoDAOImpl;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.servicio.PrediccionRiesgoService;
import pe.edu.utp.yachaykard.util.DatosSesion;
import pe.edu.utp.yachaykard.util.Zona;

import java.sql.SQLException;
import java.util.Map;

/**
 * Managed Bean del panel principal (index.xhtml).
 * Muestra un resumen general: estudiantes, matrículas activas,
 * periodo abierto e incidencias registradas.
 */
@Named("panelBean")
@RequestScoped
public class PanelBean {

    private int totalEstudiantes;
    private long matriculasActivas;
    private String periodoAbierto = "Ninguno";
    private int totalIncidencias;
    private int riesgoAlto;
    private int riesgoMedio;
    private int riesgoBajo;

    /**
     * Lee los totales desde la base de datos al abrir el panel.
     */
    @PostConstruct
    public void init() {
        try {
            totalEstudiantes = new EstudianteDAOImpl().listarTodos().size();
            matriculasActivas = new MatriculaDAOImpl().listarTodos().stream()
                    .filter(m -> "Activa".equals(m.getEstado()))
                    .count();
            periodoAbierto = new PeriodoDAOImpl().listarTodos().stream()
                    .filter(p -> "Abierto".equals(p.getEstado()))
                    .map(Periodo::getNombrePeriodo)
                    .findFirst()
                    .orElse("Ninguno");
            totalIncidencias = new IncidenciaDAOImpl().listarTodos().size();
            cargarRiesgo();
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo cargar el resumen: " + e.getMessage(), null));
        }
    }

    private void cargarRiesgo() throws SQLException {
        PrediccionRiesgoDAO dao = new PrediccionRiesgoDAOImpl();
        riesgoAlto = dao.listarVigentesPorNivel(PrediccionRiesgoService.ALTO).size();
        riesgoMedio = dao.listarVigentesPorNivel(PrediccionRiesgoService.MEDIO).size();
        riesgoBajo = dao.listarVigentesPorNivel(PrediccionRiesgoService.BAJO).size();
    }

    /** Recalcula el riesgo de todos los estudiantes con las reglas (solo administrador). */
    public void calcularRiesgo() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (!DatosSesion.ADMINISTRADOR.equals(contexto.getExternalContext().getSessionMap().get(DatosSesion.ROL))) {
            return;
        }
        try {
            Map<String, Integer> conteo = new PrediccionRiesgoService().calcularTodos(Zona.hoy());
            cargarRiesgo();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Riesgo calculado: " + conteo.get(PrediccionRiesgoService.ALTO) + " alto, "
                            + conteo.get(PrediccionRiesgoService.MEDIO) + " medio y "
                            + conteo.get(PrediccionRiesgoService.BAJO) + " bajo.", null));
        } catch (SQLException e) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo calcular el riesgo: " + e.getMessage(), null));
        }
    }

    public int getTotalEstudiantes() {
        return totalEstudiantes;
    }

    public long getMatriculasActivas() {
        return matriculasActivas;
    }

    public String getPeriodoAbierto() {
        return periodoAbierto;
    }

    public int getTotalIncidencias() {
        return totalIncidencias;
    }

    public int getRiesgoAlto() {
        return riesgoAlto;
    }

    public int getRiesgoMedio() {
        return riesgoMedio;
    }

    public int getRiesgoBajo() {
        return riesgoBajo;
    }
}