package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la conclusión descriptiva. Según la norma del Minedu,
 * la debilidad (aspecto a mejorar) solo se escribe en los niveles B y C.
 */
class GeneradorConclusionTest {

    private static final String COMPETENCIA = "Resuelve problemas de cantidad";
    private static final String FORTALEZA = "Razona con claridad";
    private static final String DEBILIDAD = "Requiere reforzar los procedimientos";

    private final GeneradorConclusion generador = new GeneradorConclusion();

    @Test
    @DisplayName("Solo B y C requieren debilidad")
    void requiereDebilidadSoloEnByC() {
        assertTrue(generador.requiereDebilidad("B"));
        assertTrue(generador.requiereDebilidad("C"));
        assertFalse(generador.requiereDebilidad("A"));
        assertFalse(generador.requiereDebilidad("AD"));
        assertFalse(generador.requiereDebilidad(null));
    }

    @Test
    @DisplayName("En AD o A se escribe la fortaleza y no la debilidad")
    void nivelAltoSinDebilidad() {
        String texto = generador.generar("AD", COMPETENCIA, FORTALEZA, DEBILIDAD);

        assertEquals("El estudiante alcanzó el nivel AD en Resuelve problemas de cantidad."
                + " Fortaleza: Razona con claridad.", texto);
    }

    @Test
    @DisplayName("En B o C se escriben la fortaleza y la debilidad")
    void nivelBajoConDebilidad() {
        String texto = generador.generar("C", COMPETENCIA, FORTALEZA, DEBILIDAD);

        assertEquals("El estudiante alcanzó el nivel C en Resuelve problemas de cantidad."
                + " Fortaleza: Razona con claridad. A mejorar: Requiere reforzar los procedimientos.", texto);
    }

    @Test
    @DisplayName("Sin fortaleza, la conclusión no la menciona")
    void sinFortaleza() {
        String texto = generador.generar("B", COMPETENCIA, "  ", DEBILIDAD);

        assertFalse(texto.contains("Fortaleza"));
        assertTrue(texto.endsWith("A mejorar: Requiere reforzar los procedimientos."));
    }

    @Test
    @DisplayName("Sin fortaleza ni debilidad queda solo el nivel")
    void soloNivel() {
        assertEquals("El estudiante alcanzó el nivel A en Resuelve problemas de cantidad.",
                generador.generar("A", COMPETENCIA, null, null));
    }

    @Test
    @DisplayName("Las frases predefinidas caben en la columna de la base de datos (150 caracteres)")
    void frasesCabenEnLaColumna() {
        GeneradorConclusion.FORTALEZAS.forEach(f -> assertTrue(f.length() <= 150));
        GeneradorConclusion.DEBILIDADES.forEach(d -> assertTrue(d.length() <= 150));
    }
}