package pe.edu.utp.yachaykard.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.utp.yachaykard.dao.impl.AsistenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CalificacionDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CompetenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.IncidenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.PeriodoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.TipoIncidenciaDAOImpl;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Calificacion;
import pe.edu.utp.yachaykard.modelo.Competencia;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.modelo.TipoIncidencia;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Servicio REST del historial del estudiante (RF-33).
 * GET /api/historial/{idEstudiante} devuelve en JSON sus calificaciones,
 * asistencias e incidencias. Requiere sesión de administrador o del tutor
 * de la sección del estudiante.
 */
@Path("historial")
public class HistorialResource {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Context
    private HttpServletRequest request;

    /**
     * Devuelve el historial completo de un estudiante.
     *
     * @param idEstudiante id del estudiante
     * @return 200 con el historial; 401, 403 o 404 con un mensaje
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
            Estudiante estudiante = new EstudianteDAOImpl().buscarPorId(idEstudiante);
            if (estudiante == null) {
                return ControlAcceso.error(404, "El estudiante no existe.");
            }

            Map<String, Object> historial = new LinkedHashMap<>();
            historial.put("estudiante", datosEstudiante(estudiante));
            historial.put("calificaciones", calificaciones(idEstudiante));
            historial.put("asistencias", asistencias(idEstudiante));
            historial.put("incidencias", incidencias(idEstudiante));
            return Response.ok(historial).build();
        } catch (SQLException e) {
            return ControlAcceso.error(500, "Error al consultar la base de datos.");
        }
    }

    private Map<String, Object> datosEstudiante(Estudiante e) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("idEstudiante", e.getIdEstudiante());
        datos.put("dni", e.getDni());
        datos.put("nombres", e.getNombres());
        datos.put("apellidos", e.getApellidos());
        datos.put("codigoEstudiante", e.getCodigoEstudiante());
        return datos;
    }

    private List<Map<String, Object>> calificaciones(int idEstudiante) throws SQLException {
        List<Calificacion> lista = new CalificacionDAOImpl().listarPorEstudiante(idEstudiante);
        lista.sort(Comparator.comparingInt(Calificacion::getIdPeriodo).thenComparingInt(Calificacion::getMes));

        Map<Integer, Competencia> competencias = new TreeMap<>();
        Map<Integer, String> cursos = new TreeMap<>();
        Map<Integer, String> periodos = new TreeMap<>();
        for (Periodo p : new PeriodoDAOImpl().listarTodos()) {
            periodos.put(p.getIdPeriodo(), p.getNombrePeriodo());
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Calificacion c : lista) {
            Competencia competencia = competencias.get(c.getIdCompetencia());
            if (competencia == null) {
                competencia = new CompetenciaDAOImpl().buscarPorId(c.getIdCompetencia());
                competencias.put(c.getIdCompetencia(), competencia);
            }
            String curso = "";
            if (competencia != null) {
                curso = cursos.get(competencia.getIdCurso());
                if (curso == null) {
                    Curso encontrado = new CursoDAOImpl().buscarPorId(competencia.getIdCurso());
                    curso = encontrado != null ? encontrado.getNombreCurso() : "";
                    cursos.put(competencia.getIdCurso(), curso);
                }
            }

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("periodo", periodos.getOrDefault(c.getIdPeriodo(), ""));
            fila.put("mes", c.getMes());
            fila.put("curso", curso);
            fila.put("competencia", competencia != null ? competencia.getDescripcion() : "");
            fila.put("nivel", c.getNivelMensual());
            fila.put("conclusion", c.getConclusionDescriptiva());
            resultado.add(fila);
        }
        return resultado;
    }

    private Map<String, Object> asistencias(int idEstudiante) throws SQLException {
        List<Asistencia> lista = new AsistenciaDAOImpl().listarPorEstudiante(idEstudiante);
        lista.sort(Comparator.comparing(Asistencia::getFecha).reversed());

        Map<String, Integer> resumen = new TreeMap<>();
        List<Map<String, Object>> registros = new ArrayList<>();
        for (Asistencia a : lista) {
            resumen.merge(a.getEstado(), 1, Integer::sum);
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("fecha", a.getFecha().format(FECHA));
            fila.put("estado", a.getEstado());
            fila.put("justificada", a.getIdJustificacion() != null);
            registros.add(fila);
        }

        Map<String, Object> asistencia = new LinkedHashMap<>();
        asistencia.put("total", lista.size());
        asistencia.put("resumen", resumen);
        asistencia.put("registros", registros);
        return asistencia;
    }

    private List<Map<String, Object>> incidencias(int idEstudiante) throws SQLException {
        List<Incidencia> lista = new IncidenciaDAOImpl().listarPorEstudiante(idEstudiante);
        lista.sort(Comparator.comparing(Incidencia::getFechaHora).reversed());

        Map<Integer, String> tipos = new TreeMap<>();
        for (TipoIncidencia t : new TipoIncidenciaDAOImpl().listarTodos()) {
            tipos.put(t.getIdTipoIncidencia(), t.getNombre());
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Incidencia i : lista) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("fecha", i.getFechaHora().format(FECHA_HORA));
            fila.put("tipo", tipos.getOrDefault(i.getIdTipoIncidencia(), ""));
            fila.put("gravedad", i.getGravedad());
            fila.put("accionTomada", i.getAccionTomada());
            fila.put("descripcion", i.getDescripcion());
            resultado.add(fila);
        }
        return resultado;
    }
}