package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.NotificacionDAO;
import pe.edu.utp.yachaykard.modelo.Apoderado;
import pe.edu.utp.yachaykard.modelo.Incidencia;
import pe.edu.utp.yachaykard.modelo.Notificacion;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de la Bandeja del tutor (RF-24) y de los avisos por WhatsApp (RF-28),
 * con el DAO simulado en Mockito. Datos ficticios.
 */
class BandejaTutorTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 20, 9, 0);
    private static final int TUTOR = 2;

    private NotificacionDAO dao;
    private BandejaTutor bandeja;
    private Apoderado apoderado;

    @BeforeEach
    void preparar() {
        dao = mock(NotificacionDAO.class);
        bandeja = new BandejaTutor(dao);
        apoderado = new Apoderado();
        apoderado.setIdApoderado(3);
        apoderado.setNombres("Jorge Huamán Cruz");
        apoderado.setTelefono("976543210");
    }

    private static Incidencia incidencia(int id, String gravedad) {
        Incidencia i = new Incidencia();
        i.setIdIncidencia(id);
        i.setIdEstudiante(4);
        i.setGravedad(gravedad);
        i.setAccionTomada("Citación al apoderado");
        i.setFechaHora(AHORA.minusDays(1));
        return i;
    }

    private static Notificacion alerta(String tipo, String estado) {
        Notificacion n = new Notificacion();
        n.setIdNotificacion(8);
        n.setIdEstudiante(4);
        n.setCanal("Interna");
        n.setTipo(tipo);
        n.setEstado(estado);
        return n;
    }

    @Test
    @DisplayName("Solo las incidencias Moderadas y Graves se avisan al apoderado")
    void estadoDelAviso() {
        Set<String> enviados = Set.of("Aviso incidencia 7");

        assertEquals("—", bandeja.estadoAviso(incidencia(5, "Leve"), enviados));
        assertEquals("Pendiente de avisar", bandeja.estadoAviso(incidencia(6, "Moderada"), enviados));
        assertEquals("Enviado", bandeja.estadoAviso(incidencia(7, "Grave"), enviados));
    }

    @Test
    @DisplayName("Avisar una incidencia guarda el aviso de WhatsApp y devuelve el enlace")
    void avisaIncidencia() throws SQLException {
        String enlace = bandeja.avisarIncidencia(incidencia(6, "Grave"), apoderado, "Diego Huamán Cruz",
                "I.E. Demo", "Agresión", TUTOR, AHORA);

        assertTrue(enlace.startsWith("https://wa.me/51976543210?text="));
        verify(dao).insertar(argThat((Notificacion n) -> "WhatsApp".equals(n.getCanal())
                && "Aviso incidencia 6".equals(n.getTipo()) && "Enviado".equals(n.getEstado())
                && n.getIdApoderado() == 3 && n.getIdUsuario() == TUTOR && n.getIdEstudiante() == 4
                && AHORA.equals(n.getFechaEnvio())));
    }

    @Test
    @DisplayName("Una incidencia Leve o un apoderado sin celular no se avisan")
    void noAvisa() throws SQLException {
        Incidencia leve = incidencia(5, "Leve");
        assertThrows(IllegalArgumentException.class,
                () -> bandeja.avisarIncidencia(leve, apoderado, "Diego", "I.E. Demo", "Tardanza", TUTOR, AHORA));

        apoderado.setTelefono(null);
        Incidencia grave = incidencia(6, "Grave");
        assertThrows(IllegalArgumentException.class,
                () -> bandeja.avisarIncidencia(grave, apoderado, "Diego", "I.E. Demo", "Agresión", TUTOR, AHORA));
        verify(dao, never()).insertar(any());
    }

    @Test
    @DisplayName("Avisar una alerta de faltas la deja Atendida")
    void avisaAlerta() throws SQLException {
        Notificacion faltas = alerta("Semana de faltas", "No leída");

        String enlace = bandeja.avisarAlerta(faltas, apoderado, "Diego Huamán Cruz", "I.E. Demo", TUTOR, AHORA);

        assertTrue(enlace.startsWith("https://wa.me/51976543210"));
        assertEquals("Atendida", faltas.getEstado());
        verify(dao).actualizar(faltas);
        verify(dao).insertar(argThat((Notificacion n) -> "Aviso Semana de faltas".equals(n.getTipo())));
    }

    @Test
    @DisplayName("Las alertas SíseVe o ya atendidas no se avisan al apoderado")
    void alertasNoAvisables() throws SQLException {
        assertFalse(BandejaTutor.alertaAvisable(alerta("Incidencia SíseVe", "No leída")));
        assertFalse(BandejaTutor.alertaAvisable(alerta("Riesgo alto", "Atendida")));
        assertTrue(BandejaTutor.alertaAvisable(alerta("Riesgo alto", "Leída")));

        Notificacion siseve = alerta("Incidencia SíseVe", "No leída");
        assertThrows(IllegalArgumentException.class,
                () -> bandeja.avisarAlerta(siseve, apoderado, "Diego", "I.E. Demo", TUTOR, AHORA));
        verify(dao, never()).insertar(any());
    }

    @Test
    @DisplayName("Marcar como leída solo cambia las alertas sin leer")
    void marcaLeida() throws SQLException {
        Notificacion nueva = alerta("Riesgo alto", "No leída");
        bandeja.marcarLeida(nueva);
        assertEquals("Leída", nueva.getEstado());
        verify(dao).actualizar(nueva);

        Notificacion atendida = alerta("Riesgo alto", "Atendida");
        bandeja.marcarLeida(atendida);
        assertEquals("Atendida", atendida.getEstado());
        verify(dao, never()).actualizar(atendida);
    }

    @Test
    @DisplayName("La bandeja muestra solo las alertas internas y reconoce los avisos enviados")
    void alertasYAvisos() throws SQLException {
        Notificacion interna = alerta("Riesgo alto", "No leída");
        Notificacion whatsapp = alerta("Aviso incidencia 6", "Enviado");
        whatsapp.setCanal("WhatsApp");
        when(dao.listarPorUsuario(TUTOR)).thenReturn(List.of(interna, whatsapp));
        when(dao.listarTodos()).thenReturn(List.of(interna, whatsapp));

        assertEquals(List.of(interna), bandeja.alertas(TUTOR));
        assertEquals(Set.of("Aviso incidencia 6"), bandeja.avisosEnviados());
    }
}
