package pe.edu.utp.yachaykard.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.utp.yachaykard.modelo.Usuario;
import pe.edu.utp.yachaykard.servicio.AccesoEstudiantes;
import pe.edu.utp.yachaykard.util.DatosSesion;

import java.sql.SQLException;
import java.util.Map;

/**
 * Control de acceso común a los servicios REST (RF-03).
 * Lee el usuario y el rol guardados en la sesión al iniciar sesión.
 */
final class ControlAcceso {

    private ControlAcceso() {
    }

    /**
     * Verifica que haya sesión y que el usuario pueda consultar al estudiante.
     *
     * @param request      petición HTTP
     * @param idEstudiante estudiante a consultar
     * @return respuesta 401, 403 o 500 si se niega el acceso; null si está permitido
     */
    static Response verificar(HttpServletRequest request, int idEstudiante) {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = sesion != null ? (Usuario) sesion.getAttribute(DatosSesion.USUARIO) : null;
        if (usuario == null) {
            return error(401, "Debe iniciar sesión.");
        }
        String rol = (String) sesion.getAttribute(DatosSesion.ROL);
        try {
            if (!new AccesoEstudiantes().puedeConsultar(usuario, rol, idEstudiante)) {
                return error(403, "No tiene permiso para consultar a este estudiante.");
            }
        } catch (SQLException e) {
            return error(500, "Error al consultar la base de datos.");
        }
        return null;
    }

    /**
     * Arma una respuesta JSON con un mensaje de error.
     *
     * @param estado  código HTTP
     * @param mensaje texto para el usuario
     * @return respuesta {"mensaje": "..."}
     */
    static Response error(int estado, String mensaje) {
        return Response.status(estado)
                .entity(Map.of("mensaje", mensaje))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}