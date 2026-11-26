package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del modelo de Weka (RF-26). Entrena el árbol J48 con el archivo de datos ficticios
 * de src/main/resources; no necesitan MySQL.
 */
class ModeloRiesgoWekaTest {

    @Test
    @DisplayName("Sin señales de alerta el modelo da riesgo Bajo")
    void sinSenalesBajo() {
        assertEquals("Bajo", ModeloRiesgoWeka.obtener().clasificar(0, 0, 0));
    }

    @Test
    @DisplayName("Con las tres señales el modelo da riesgo Alto")
    void tresSenalesAlto() {
        assertEquals("Alto", ModeloRiesgoWeka.obtener().clasificar(8, 3, 2));
    }

    @Test
    @DisplayName("El modelo siempre responde Alto, Medio o Bajo")
    void respondeNivelValido() {
        Set<String> niveles = Set.of("Alto", "Medio", "Bajo");
        assertTrue(niveles.contains(ModeloRiesgoWeka.obtener().clasificar(6, 0, 0)));
        assertTrue(niveles.contains(ModeloRiesgoWeka.obtener().clasificar(3, 1, 0)));
    }

    @Test
    @DisplayName("El árbol aprendido se puede mostrar para el informe")
    void arbolEnTexto() {
        assertFalse(ModeloRiesgoWeka.obtener().descripcionArbol().isBlank());
    }

    @Test
    @DisplayName("Sin archivo o con un archivo dañado no se entrena")
    void sinDatos() {
        assertThrows(IllegalStateException.class, () -> new ModeloRiesgoWeka(null));
        ByteArrayInputStream danado = new ByteArrayInputStream("esto no es ARFF".getBytes(StandardCharsets.UTF_8));
        assertThrows(IllegalStateException.class, () -> new ModeloRiesgoWeka(danado));
    }
}