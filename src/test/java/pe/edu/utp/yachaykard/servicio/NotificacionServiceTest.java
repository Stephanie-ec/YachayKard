package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AsistenciaDAO;
import pe.edu.utp.yachaykard.dao.DestinatariosDAO;
import pe.edu.utp.yachaykard.dao.IncidenciaDAO;
import pe.edu.utp.yachaykard.dao.NotificacionDAO;
import pe.edu.utp.yachaykard.dao.PrediccionRiesgoDAO;
import pe.edu.utp.yachaykard.modelo.Asistencia;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.Notificacion;
import pe.edu.utp.yachaykard.modelo.PrediccionRiesgo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las alertas internas (RF-27), con los DAO simulados en Mockito. Datos ficticios.
 */
class NotificacionServiceTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 20, 9, 0);
    private static final LocalDate HOY = AHORA.toLocalDate();
    private static final int ADMIN = 1;
    private static final int TUTOR = 2;
    private static final int ESTUDIANTE = 4;

    private DestinatariosDAO destinatariosDAO;
    private AsistenciaDAO asistenciaDAO;
    private IncidenciaDAO incidenciaDAO;
    private PrediccionRiesgoDAO prediccionDAO;
    private NotificacionDAO notificacionDAO;
    private NotificacionService servicio;

    @BeforeEach
    void preparar() throws SQLException {
        destinatariosDAO = mock(DestinatariosDAO.class);
        asistenciaDAO = mock(AsistenciaDAO.class);
        incidenciaDAO = mock(IncidenciaDAO.class);
        prediccionDAO = mock(PrediccionRiesgoDAO.class);
        notificacionDAO = mock(NotificacionDAO.class);
        servicio = new NotificacionService(destinatariosDAO, asistenciaDAO, incidenciaDAO, prediccionDAO, notificacionDAO);
        when(destinatariosDAO.listarAdministradores()).thenReturn(List.of(ADMIN));
        when(destinatariosDAO.listarDestinos("2026")).thenReturn(
                List.of(new DestinatariosDAO.Destino(ESTUDIANTE, "Castillo Núñez, Mateo", TUTOR)));
    }

    private static List<Asistencia> faltas(int cantidad, Integer idJustificacion) {
        List<Asistencia> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Asistencia a = new Asistencia();
            a.setFecha(HOY.minusDays(i));
            a.setEstado("Falta");
            a.setIdJustificacion(idJustificacion);
            lista.add(a);
        }
        return lista;
    }

    private static Incidencia incidencia(boolean siseve, int haceDias) {
        Incidencia i = new Incidencia();
        i.setRequiereSiseve(siseve);
        i.setFechaHora(AHORA.minusDays(haceDias));
        return i;
    }

    private static PrediccionRiesgo riesgo(String nivel) {
        PrediccionRiesgo p = new PrediccionRiesgo();
        p.setNivelRiesgo(nivel);
        p.setMotivo("Modelo J48: 5 faltas sin justificar en los últimos 30 días.");
        return p;
    }

    @Test
    @DisplayName("Una semana de faltas avisa al tutor y al administrador")
    void semanaDeFaltas() throws SQLException {
        when(asistenciaDAO.listarPorEstudiante(ESTUDIANTE)).thenReturn(faltas(5, null));

        assertEquals(2, servicio.generar(AHORA));

        verify(notificacionDAO).insertar(argThat((Notificacion n) -> n.getIdUsuario() == ADMIN
                && NotificacionService.TIPO_FALTAS.equals(n.getTipo())
                && NotificacionService.CANAL_INTERNA.equals(n.getCanal())
                && NotificacionService.NO_LEIDA.equals(n.getEstado())
                && n.getMensaje().startsWith("Castillo Núñez, Mateo: 5 faltas")));
        verify(notificacionDAO).insertar(argThat((Notificacion n) -> n.getIdUsuario() == TUTOR));
    }

    @Test
    @DisplayName("Cuatro faltas o faltas justificadas no generan alerta")
    void pocasFaltasNoAvisan() throws SQLException {
        List<Asistencia> registros = new ArrayList<>(faltas(4, null));
        registros.addAll(faltas(3, 9));
        when(asistenciaDAO.listarPorEstudiante(ESTUDIANTE)).thenReturn(registros);

        assertEquals(0, servicio.generar(AHORA));
        verify(notificacionDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Una incidencia para el SíseVe de esta semana genera alerta; una antigua no")
    void incidenciaSiseve() throws SQLException {
        when(incidenciaDAO.listarPorEstudiante(ESTUDIANTE)).thenReturn(List.of(
                incidencia(true, 2), incidencia(false, 1), incidencia(true, 10)));

        servicio.generar(AHORA);

        verify(notificacionDAO, times(2)).insertar(argThat((Notificacion n) ->
                NotificacionService.TIPO_SISEVE.equals(n.getTipo())
                        && n.getMensaje().contains("1 incidencia para reportar en el SíseVe")));
    }

    @Test
    @DisplayName("El riesgo Alto genera alerta con el motivo; el riesgo Medio no")
    void riesgoAlto() throws SQLException {
        when(prediccionDAO.buscarUltimaPorEstudiante(ESTUDIANTE)).thenReturn(riesgo("Alto"));
        servicio.generar(AHORA);
        verify(notificacionDAO, times(2)).insertar(argThat((Notificacion n) ->
                NotificacionService.TIPO_RIESGO.equals(n.getTipo()) && n.getMensaje().contains("Modelo J48")));

        when(prediccionDAO.buscarUltimaPorEstudiante(ESTUDIANTE)).thenReturn(riesgo("Medio"));
        assertEquals(0, servicio.generar(AHORA));
    }

    @Test
    @DisplayName("Una alerta ya enviada esta semana no se repite")
    void noRepite() throws SQLException {
        when(asistenciaDAO.listarPorEstudiante(ESTUDIANTE)).thenReturn(faltas(5, null));
        when(notificacionDAO.existeAlerta(ESTUDIANTE, ADMIN, NotificacionService.TIPO_FALTAS, AHORA.minusDays(7)))
                .thenReturn(true);

        assertEquals(1, servicio.generar(AHORA));
        verify(notificacionDAO).insertar(argThat((Notificacion n) -> n.getIdUsuario() == TUTOR));
    }

    @Test
    @DisplayName("Si la sección no tiene tutor, solo se avisa al administrador")
    void sinTutor() throws SQLException {
        when(destinatariosDAO.listarDestinos("2026")).thenReturn(
                List.of(new DestinatariosDAO.Destino(ESTUDIANTE, "Castillo Núñez, Mateo", null)));
        when(asistenciaDAO.listarPorEstudiante(ESTUDIANTE)).thenReturn(faltas(6, null));

        assertEquals(1, servicio.generar(AHORA));
    }

    @Test
    @DisplayName("Pendientes son solo las alertas internas sin leer")
    void pendientes() throws SQLException {
        Notificacion sinLeer = new Notificacion();
        sinLeer.setCanal("Interna");
        sinLeer.setEstado("No leída");
        Notificacion leida = new Notificacion();
        leida.setCanal("Interna");
        leida.setEstado("Leída");
        Notificacion whatsapp = new Notificacion();
        whatsapp.setCanal("WhatsApp");
        whatsapp.setEstado("Enviado");
        when(notificacionDAO.listarPorUsuario(TUTOR)).thenReturn(List.of(sinLeer, leida, whatsapp));

        List<Notificacion> pendientes = servicio.pendientes(TUTOR);

        assertEquals(1, pendientes.size());
        assertTrue(pendientes.contains(sinLeer));
    }
}