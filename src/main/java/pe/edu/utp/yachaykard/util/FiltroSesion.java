package pe.edu.utp.yachaykard.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Controla el acceso a las pantallas (RF-03).
 * Sin sesión iniciada, redirige a login.xhtml. Si el rol del usuario
 * no tiene permiso para la pantalla pedida, lo devuelve al panel principal.
 */
@WebFilter("*.xhtml")
public class FiltroSesion implements Filter {

    private static final Set<String> SOLO_ADMIN = Set.of(DatosSesion.ADMINISTRADOR);
    private static final Set<String> ADMIN_Y_DOCENTE = Set.of(DatosSesion.ADMINISTRADOR, DatosSesion.DOCENTE);

    /** Pantallas restringidas y los roles que pueden abrirlas. Las demás las abre cualquier usuario con sesión. */
    private static final Map<String, Set<String>> PERMISOS = Map.ofEntries(
            Map.entry("/institucion.xhtml", SOLO_ADMIN),
            Map.entry("/estudiantes.xhtml", SOLO_ADMIN),
            Map.entry("/periodos.xhtml", SOLO_ADMIN),
            Map.entry("/catalogoIncidencias.xhtml", SOLO_ADMIN),
            Map.entry("/usuarios.xhtml", SOLO_ADMIN),
            Map.entry("/estructura.xhtml", SOLO_ADMIN),
            Map.entry("/matriculas.xhtml", SOLO_ADMIN),
            Map.entry("/asignaciones.xhtml", SOLO_ADMIN),
            Map.entry("/calificaciones.xhtml", ADMIN_Y_DOCENTE),
            Map.entry("/cierre.xhtml", ADMIN_Y_DOCENTE),
            Map.entry("/historial.xhtml", ADMIN_Y_DOCENTE),
            Map.entry("/misSecciones.xhtml", Set.of(DatosSesion.DOCENTE)),
            Map.entry("/asistencia.xhtml", Set.of(DatosSesion.ADMINISTRADOR, DatosSesion.AUXILIAR)),
            Map.entry("/incidencias.xhtml",
                    Set.of(DatosSesion.ADMINISTRADOR, DatosSesion.DOCENTE, DatosSesion.AUXILIAR))
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String contexto = req.getContextPath();
        String ruta = req.getRequestURI().substring(contexto.length());

        HttpSession sesion = req.getSession(false);
        boolean conSesion = sesion != null && sesion.getAttribute(DatosSesion.USUARIO) != null;

        if (ruta.equals("/login.xhtml")) {
            if (conSesion) {
                resp.sendRedirect(contexto + "/index.xhtml");
            } else {
                chain.doFilter(request, response);
            }
            return;
        }

        if (!conSesion) {
            resp.sendRedirect(contexto + "/login.xhtml");
            return;
        }

        Set<String> rolesPermitidos = PERMISOS.get(ruta);
        if (rolesPermitidos != null && !rolesPermitidos.contains(sesion.getAttribute(DatosSesion.ROL))) {
            resp.sendRedirect(contexto + "/index.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}