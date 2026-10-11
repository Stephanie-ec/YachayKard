package pe.edu.utp.yachaykard.modelo;

import java.io.Serializable;


/* Datos generales de la institución educativa que usa YachayKard. */
public class Institucion implements Serializable {

    private int idInstitucion;
    private String nombre;
    private String codigoModular;
    private String nivel;
    private String nombreDirector;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String telefono;
    private String correoElectronico;

    /** Constructor vacío requerido por JSF y por el DAO al mapear filas. */
    public Institucion() {
    }

    /**
     * Crea una institución con todos sus datos.
     *
     * @param idInstitucion     identificador de la institución
     * @param nombre            nombre oficial del colegio
     * @param codigoModular     código modular asignado por el Minedu
     * @param nivel             nivel educativo (por ejemplo, Secundaria)
     * @param nombreDirector    nombre del director
     * @param direccion         dirección del local
     * @param departamento      departamento
     * @param provincia         provincia
     * @param distrito          distrito
     * @param telefono          teléfono de contacto
     * @param correoElectronico correo institucional
     */
    public Institucion(int idInstitucion, String nombre, String codigoModular, String nivel,
                       String nombreDirector, String direccion, String departamento, String provincia,
                       String distrito, String telefono, String correoElectronico) {
        this.idInstitucion = idInstitucion;
        this.nombre = nombre;
        this.codigoModular = codigoModular;
        this.nivel = nivel;
        this.nombreDirector = nombreDirector;
        this.direccion = direccion;
        this.departamento = departamento;
        this.provincia = provincia;
        this.distrito = distrito;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoModular() {
        return codigoModular;
    }

    public void setCodigoModular(String codigoModular) {
        this.codigoModular = codigoModular;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNombreDirector() {
        return nombreDirector;
    }

    public void setNombreDirector(String nombreDirector) {
        this.nombreDirector = nombreDirector;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    @Override
    public String toString() {
        return "Institucion{" +
                "idInstitucion=" + idInstitucion +
                ", nombre='" + nombre + '\'' +
                ", codigoModular='" + codigoModular + '\'' +
                ", nivel='" + nivel + '\'' +
                '}';
    }
}