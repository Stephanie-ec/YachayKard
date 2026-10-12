package pe.edu.utp.yachaykard.util;

/**
 * Nombres de los atributos que se guardan en la sesión HTTP al iniciar sesión
 * y nombres de los roles del sistema. Los usan LoginBean, SesionBean,
 * FiltroSesion y los servicios REST, así todos leen la sesión de la misma forma.
 */
public final class DatosSesion {

    /** Usuario autenticado (objeto Usuario, sin contraseña). */
    public static final String USUARIO = "usuario";
    /** Nombre del rol del usuario. */
    public static final String ROL = "rol";
    /** Nombre de la institución del usuario. */
    public static final String INSTITUCION = "institucion";
    /** Id del registro en la tabla sesion. */
    public static final String ID_SESION = "idSesion";

    public static final String ADMINISTRADOR = "Administrador";
    public static final String DOCENTE = "Docente";
    public static final String AUXILIAR = "Auxiliar";

    private DatosSesion() {
    }
}