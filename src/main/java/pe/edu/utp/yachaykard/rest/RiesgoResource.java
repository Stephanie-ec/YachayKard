package pe.edu.utp.yachaykard.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PrediccionRiesgoDAOImpl;
import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servicio REST del riesgo de deserción (RF-33).
 * GET /api/riesgo/{idEstudiante} devuelve en JSON la última predicción
 * registrada. Requiere sesión de administrador o del tutor de la sección.
 */
@Path("riesgo")
public class RiesgoResource {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Context
    private HttpServletRequest request;

    /**
     * Devuelve la última predicción de riesgo del estudiante.
     *
     * @param idEstudiante id del estudiante
     * @return 200 con el riesgo; 401, 403 o 404 con un mensaje
     */
    @GET
    @Path("{idEstudiante}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtener(@PathParam("idEstudiante") int idEstudiante) {
        Response denegado = ControlAcceso.verificar(request, idEstudiante);
        if (denegado != null) {
            return denegado;
        }
        try {
            if (new EstudianteDAOImpl().buscarPorId(idEstudiante) == null) {
                return ControlAcceso.error(404, "El estudiante no existe.");
            }
            PrediccionRiesgo prediccion = new PrediccionRiesgoDAOImpl().buscarUltimaPorEstudiante(idEstudiante);

            Map<String, Object> riesgo = new LinkedHashMap<>();
            riesgo.put("idEstudiante", idEstudiante);
            if (prediccion == null) {
                riesgo.put("nivelRiesgo", "Sin calcular");
                riesgo.put("motivo", "Aún no se ha calculado el riesgo de este estudiante.");
                riesgo.put("fechaCalculo", null);
            } else {
                riesgo.put("nivelRiesgo", prediccion.getNivelRiesgo());
                riesgo.put("motivo", prediccion.getMotivo());
                riesgo.put("fechaCalculo", prediccion.getFechaCalculo().format(FECHA));
            }
            return Response.ok(riesgo).build();
        } catch (SQLException e) {
            return ControlAcceso.error(500, "Error al consultar la base de datos.");
        }
    }
}