package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.MatriculaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.modelo.Periodo;

import java.sql.SQLException;

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
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo cargar el resumen: " + e.getMessage(), null));
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
}