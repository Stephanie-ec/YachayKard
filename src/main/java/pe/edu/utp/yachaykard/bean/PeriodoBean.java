package pe.edu.utp.yachaykard.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import pe.edu.utp.yachaykard.dao.PeriodoDAO;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.modelo.Periodo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean de la pantalla "Periodos académicos" (periodos.xhtml).
 * El administrador registra bimestres o trimestres y los abre o cierra.
 * Es un mantenimiento simple, por eso llama directo al DAO ("CRUD directo").
 */
@Named("periodoBean")
@ViewScoped
public class PeriodoBean implements Serializable {

    private List<Periodo> periodos = new ArrayList<>();
    private Periodo periodo = nuevoPeriodo();

    /**
     * Carga la lista de periodos al abrir la pantalla.
     */
    @PostConstruct
    public void init() {
        cargar();
    }

    /**
     * Vuelve a leer los periodos desde la base de datos.
     */
    public void cargar() {
        try {
            periodos = periodoDAO().listarTodos();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    /**
     * Registra un periodo nuevo o actualiza el que se está editando.
     */
    public void guardar() {
        if (periodo.getFechaFin().isBefore(periodo.getFechaInicio())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La fecha de fin no puede ser anterior a la fecha de inicio.");
            return;
        }
        try {
            if (periodo.getIdPeriodo() == 0) {
                periodoDAO().insertar(periodo);
                mensaje(FacesMessage.SEVERITY_INFO, "Periodo registrado correctamente.");
            } else {
                periodoDAO().actualizar(periodo);
                mensaje(FacesMessage.SEVERITY_INFO, "Periodo actualizado correctamente.");
            }
            periodo = nuevoPeriodo();
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el periodo: " + e.getMessage());
        }
    }

    /**
     * Abre un periodo para que los docentes registren calificaciones.
     *
     * @param idPeriodo periodo a abrir
     */
    public void abrir(int idPeriodo) {
        try {
            periodoDAO().abrirPeriodo(idPeriodo);
            mensaje(FacesMessage.SEVERITY_INFO, "Periodo abierto.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo abrir el periodo: " + e.getMessage());
        }
    }

    /**
     * Cierra un periodo; sus calificaciones ya no se podrán editar.
     *
     * @param idPeriodo periodo a cerrar
     */
    public void cerrar(int idPeriodo) {
        try {
            periodoDAO().cerrarPeriodo(idPeriodo);
            mensaje(FacesMessage.SEVERITY_INFO, "Periodo cerrado.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cerrar el periodo: " + e.getMessage());
        }
    }

    /**
     * Carga un periodo en el formulario para editarlo.
     *
     * @param seleccionado periodo elegido en la tabla
     */
    public void editar(Periodo seleccionado) {
        periodo = seleccionado;
    }

    /**
     * Elimina un periodo.
     *
     * @param idPeriodo id del periodo a eliminar
     */
    public void eliminar(int idPeriodo) {
        try {
            periodoDAO().eliminar(idPeriodo);
            mensaje(FacesMessage.SEVERITY_INFO, "Periodo eliminado.");
            cargar();
        } catch (SQLException e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No se puede eliminar: el periodo ya tiene calificaciones registradas.");
        }
    }

    /**
     * Limpia el formulario.
     */
    public void cancelar() {
        periodo = nuevoPeriodo();
    }

    /**
     * @return periodo vacío con estado inicial "Abierto"
     */
    private Periodo nuevoPeriodo() {
        Periodo p = new Periodo();
        p.setEstado("Abierto");
        return p;
    }

    /**
     * @return DAO de periodos (se crea en cada uso porque no guarda estado)
     */
    private PeriodoDAO periodoDAO() {
        return new PeriodoDAOImpl();
    }

    /**
     * Muestra un mensaje en la pantalla.
     *
     * @param severidad tipo de mensaje (información o error)
     * @param texto     texto a mostrar
     */
    private void mensaje(FacesMessage.Severity severidad, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
}