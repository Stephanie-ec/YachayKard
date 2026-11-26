package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del aviso al apoderado por WhatsApp (RF-28). Datos ficticios.
 */
class AvisoWhatsAppTest {

    private final AvisoWhatsApp aviso = new AvisoWhatsApp();

    @Test
    @DisplayName("El enlace lleva el código del Perú y el mensaje codificado")
    void enlaceConMensaje() {
        assertEquals("https://wa.me/51987654321?text=Hola%20tutor",
                aviso.enlace("987654321", "Hola tutor"));
    }

    @Test
    @DisplayName("Acepta el celular con espacios, guiones o +51")
    void normalizaCelular() {
        assertEquals("987654321", aviso.normalizarCelular("+51 987 654 321"));
        assertEquals("987654321", aviso.normalizarCelular("987-654-321"));
        assertEquals("987654321", aviso.normalizarCelular("51987654321"));
    }

    @Test
    @DisplayName("Un teléfono vacío, fijo o incompleto no arma el enlace")
    void celularInvalido() {
        assertThrows(IllegalArgumentException.class, () -> aviso.enlace(null, "Hola"));
        assertThrows(IllegalArgumentException.class, () -> aviso.enlace("", "Hola"));
        assertThrows(IllegalArgumentException.class, () -> aviso.enlace("014567890", "Hola"));
        assertThrows(IllegalArgumentException.class, () -> aviso.enlace("98765432", "Hola"));
        assertFalse(aviso.esCelularValido("12345"));
        assertTrue(aviso.esCelularValido("965432109"));
    }

    @Test
    @DisplayName("Las tildes y la ñ se codifican para que WhatsApp las muestre bien")
    void codificaTildes() {
        String enlace = aviso.enlace("987654321", "Núñez faltó");
        assertTrue(enlace.endsWith("?text=N%C3%BA%C3%B1ez%20falt%C3%B3"));
    }

    @Test
    @DisplayName("Un mensaje vacío no arma el enlace")
    void mensajeVacio() {
        assertThrows(IllegalArgumentException.class, () -> aviso.enlace("987654321", "   "));
    }

    @Test
    @DisplayName("El mensaje de incidencia nombra al apoderado, al estudiante, la fecha y la acción")
    void mensajeIncidencia() {
        String texto = aviso.mensajeIncidencia("María Torres Salas", "Valeria Torres Salas", "I.E. Demo",
                "Falta de respeto", "Moderada", LocalDateTime.of(2026, 10, 9, 11, 0), "Derivación a tutoría");

        assertTrue(texto.startsWith("Estimado(a) María Torres Salas:"));
        assertTrue(texto.contains("Valeria Torres Salas tuvo una incidencia moderada el 09/10/2026"));
        assertTrue(texto.contains("Acción tomada: Derivación a tutoría."));
        assertTrue(texto.endsWith("tutoría de I.E. Demo."));
    }

    @Test
    @DisplayName("Solo las alertas de faltas y de riesgo alto tienen mensaje al apoderado")
    void mensajeAlerta() {
        assertTrue(aviso.mensajeAlerta("Ana", "Camila", "I.E. Demo", "Semana de faltas").contains("faltas sin justificar"));
        assertTrue(aviso.mensajeAlerta("Ana", "Camila", "I.E. Demo", "Riesgo alto").contains("acompañamiento"));
        assertThrows(IllegalArgumentException.class,
                () -> aviso.mensajeAlerta("Ana", "Camila", "I.E. Demo", "Incidencia SíseVe"));
    }
}
