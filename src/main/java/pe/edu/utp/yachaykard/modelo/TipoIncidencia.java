package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;

/**
 * Tipo de falta del catálogo de incidencias, editable según el reglamento de cada colegio.
 */
public class TipoIncidencia implements Serializable {

    private int idTipoIncidencia;
    private String nombre;
    private String gravedadSugerida;
    private boolean activo = true;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public TipoIncidencia() {
    }

    /**
     * Crea un objeto TipoIncidencia con todos sus datos.
     *
     * @param idTipoIncidencia identificador del tipo
     * @param nombre           nombre de la falta (por ejemplo, Tardanza)
     * @param gravedadSugerida gravedad que se preselecciona: Leve, Moderada o Grave
     */
    public TipoIncidencia(int idTipoIncidencia, String nombre, String gravedadSugerida) {
        this.idTipoIncidencia = idTipoIncidencia;
        this.nombre = nombre;
        this.gravedadSugerida = gravedadSugerida;
    }

    /**
     * Crea un objeto TipoIncidencia indicando también si está activo.
     *
     * @param idTipoIncidencia identificador del tipo
     * @param nombre           nombre de la falta
     * @param gravedadSugerida gravedad que se preselecciona: Leve, Moderada o Grave
     * @param activo           false si fue desactivado (ya no se ofrece al registrar incidencias)
     */
    public TipoIncidencia(int idTipoIncidencia, String nombre, String gravedadSugerida, boolean activo) {
        this(idTipoIncidencia, nombre, gravedadSugerida);
        this.activo = activo;
    }

    public int getIdTipoIncidencia() {
        return idTipoIncidencia;
    }

    public void setIdTipoIncidencia(int idTipoIncidencia) {
        this.idTipoIncidencia = idTipoIncidencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGravedadSugerida() {
        return gravedadSugerida;
    }

    public void setGravedadSugerida(String gravedadSugerida) {
        this.gravedadSugerida = gravedadSugerida;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "TipoIncidencia{" +
                "idTipoIncidencia=" + idTipoIncidencia +
                ", nombre=" + nombre +
                ", gravedadSugerida=" + gravedadSugerida +
                ", activo=" + activo +
                '}';
    }
}