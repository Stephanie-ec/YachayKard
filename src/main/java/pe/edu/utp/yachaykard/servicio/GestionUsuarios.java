package pe.edu.utp.yachaykard.servicio;

import pe.edu.utp.yachaykard.dao.AuxiliarDAO;
import pe.edu.utp.yachaykard.dao.DocenteDAO;
import pe.edu.utp.yachaykard.dao.UsuarioDAO;
import pe.edu.utp.yachaykard.dao.impl.AuxiliarDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.DocenteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.UsuarioDAOImpl;
import pe.edu.utp.yachaykard.modelo.Auxiliar;
import pe.edu.utp.yachaykard.modelo.Docente;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Reglas de la gestión de usuarios (RF-02):
 * <ul>
 *     <li>El nombre de usuario tiene de 4 a 50 caracteres (minúsculas, números, punto o guion bajo) y no se repite.</li>
 *     <li>La contraseña tiene al menos 8 caracteres, con letras y números.</li>
 *     <li>Al crear un docente o un auxiliar se registran también sus datos personales.</li>
 *     <li>Los usuarios no se eliminan: se desactivan. Nadie puede desactivar su propia cuenta.</li>
 * </ul>
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class GestionUsuarios {

    private static final Pattern USERNAME = Pattern.compile("[a-z0-9._]{4,50}");
    private static final Pattern DNI = Pattern.compile("\\d{8}");
    private static final int LARGO_MINIMO_CLAVE = 8;

    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final AuxiliarDAO auxiliarDAO;

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public GestionUsuarios() {
        this(new UsuarioDAOImpl(), new DocenteDAOImpl(), new AuxiliarDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param usuarioDAO  acceso a usuarios
     * @param docenteDAO  acceso a docentes
     * @param auxiliarDAO acceso a auxiliares
     */
    public GestionUsuarios(UsuarioDAO usuarioDAO, DocenteDAO docenteDAO, AuxiliarDAO auxiliarDAO) {
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.auxiliarDAO = auxiliarDAO;
    }

    /**
     * Registra un usuario nuevo. Si el rol es Docente o Auxiliar, también registra sus datos personales.
     *
     * @param usuario   nombre de usuario, rol e institución
     * @param nombreRol nombre del rol elegido (Administrador, Docente o Auxiliar)
     * @param clave     contraseña en texto plano
     * @param persona   datos personales (solo para Docente y Auxiliar; puede ser null para Administrador)
     * @throws IllegalArgumentException si algún dato no cumple las reglas
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void registrar(Usuario usuario, String nombreRol, String clave, DatosPersona persona) throws SQLException {
        validarUsername(usuario);
        if (usuario.getIdRol() <= 0 || nombreRol == null) {
            throw new IllegalArgumentException("Seleccione un rol.");
        }
        validarClave(clave);
        boolean requierePersona = requierePersona(nombreRol);
        if (requierePersona) {
            validarPersona(persona);
        }
        if (usuarioDAO.buscarPorUsername(usuario.getUsername()) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        }

        usuario.setPassword(clave);
        usuario.setActivo(true);
        int idUsuario = usuarioDAO.registrar(usuario);

        if (DatosSesion.DOCENTE.equals(nombreRol)) {
            Docente docente = new Docente();
            docente.setDni(persona.getDni());
            docente.setNombres(persona.getNombres().trim());
            docente.setApellidos(persona.getApellidos().trim());
            docente.setEspecialidad(persona.getEspecialidad());
            docente.setIdUsuario(idUsuario);
            docenteDAO.insertar(docente);
        } else if (DatosSesion.AUXILIAR.equals(nombreRol)) {
            Auxiliar auxiliar = new Auxiliar();
            auxiliar.setDni(persona.getDni());
            auxiliar.setNombres(persona.getNombres().trim());
            auxiliar.setApellidos(persona.getApellidos().trim());
            auxiliar.setIdUsuario(idUsuario);
            auxiliarDAO.insertar(auxiliar);
        }
    }

    /**
     * Actualiza el nombre de usuario y, si se escribe una, la contraseña.
     *
     * @param usuario    usuario con los datos editados
     * @param claveNueva contraseña nueva; vacía para conservar la actual
     * @throws IllegalArgumentException si algún dato no cumple las reglas
     * @throws SQLException             si ocurre un error en la base de datos
     */
    public void actualizar(Usuario usuario, String claveNueva) throws SQLException {
        validarUsername(usuario);
        Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (existente != null && existente.getIdUsuario() != usuario.getIdUsuario()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        }
        if (claveNueva == null || claveNueva.isEmpty()) {
            usuario.setPassword(null);
        } else {
            validarClave(claveNueva);
            usuario.setPassword(claveNueva);
        }
        usuarioDAO.actualizar(usuario);
    }

    /**
     * Activa un usuario inactivo o desactiva uno activo.
     *
     * @param usuario         usuario elegido en la tabla
     * @param idUsuarioSesion usuario que tiene la sesión abierta
     * @return true si quedó activo; false si quedó desactivado
     * @throws IllegalStateException si intenta desactivar su propia cuenta
     * @throws SQLException          si ocurre un error en la base de datos
     */
    public boolean cambiarEstado(Usuario usuario, int idUsuarioSesion) throws SQLException {
        if (usuario.getIdUsuario() == idUsuarioSesion) {
            throw new IllegalStateException("No puede desactivar su propia cuenta.");
        }
        boolean activar = !usuario.isActivo();
        usuarioDAO.cambiarEstado(usuario.getIdUsuario(), activar);
        return activar;
    }

    /**
     * @param nombreRol nombre del rol
     * @return true si el rol necesita datos personales (Docente o Auxiliar)
     */
    public boolean requierePersona(String nombreRol) {
        return DatosSesion.DOCENTE.equals(nombreRol) || DatosSesion.AUXILIAR.equals(nombreRol);
    }

    /**
     * Revisa la política de contraseñas: mínimo 8 caracteres, con al menos una letra y un número.
     *
     * @param clave contraseña a revisar
     * @throws IllegalArgumentException si no cumple
     */
    public void validarClave(String clave) {
        if (clave == null || clave.length() < LARGO_MINIMO_CLAVE
                || clave.chars().noneMatch(Character::isLetter)
                || clave.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, con letras y números.");
        }
    }

    private static void validarUsername(Usuario usuario) {
        String username = usuario.getUsername() == null ? "" : usuario.getUsername().trim().toLowerCase();
        if (!USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException(
                    "El usuario debe tener de 4 a 50 caracteres: minúsculas, números, punto o guion bajo.");
        }
        usuario.setUsername(username);
    }

    private static void validarPersona(DatosPersona persona) {
        if (persona == null || persona.getDni() == null || !DNI.matcher(persona.getDni()).matches()) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos.");
        }
        if (vacio(persona.getNombres()) || vacio(persona.getApellidos())) {
            throw new IllegalArgumentException("Los nombres y apellidos son obligatorios.");
        }
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    /** Datos personales de un docente o auxiliar que se registran junto con su usuario. */
    public static class DatosPersona implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dni;
        private String nombres;
        private String apellidos;
        private String especialidad;

        public String getDni() {
            return dni;
        }

        public void setDni(String dni) {
            this.dni = dni;
        }

        public String getNombres() {
            return nombres;
        }

        public void setNombres(String nombres) {
            this.nombres = nombres;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getEspecialidad() {
            return especialidad;
        }

        public void setEspecialidad(String especialidad) {
            this.especialidad = especialidad;
        }
    }
}