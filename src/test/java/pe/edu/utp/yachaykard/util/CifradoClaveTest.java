package pe.edu.utp.yachaykard.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del cifrado de contraseñas con PBKDF2 y sal (RNF-02).
 */
class CifradoClaveTest {

    private static final String CLAVE = "Demo2026";

    @Test
    @DisplayName("La contraseña cifrada no contiene la contraseña original")
    void noGuardaLaClaveEnTextoPlano() {
        String guardado = CifradoClave.cifrar(CLAVE);
        assertFalse(guardado.contains(CLAVE));
        assertTrue(guardado.startsWith("pbkdf2$"));
    }

    @Test
    @DisplayName("La misma contraseña da resultados distintos por la sal")
    void mismaClaveDaHashesDistintos() {
        assertNotEquals(CifradoClave.cifrar(CLAVE), CifradoClave.cifrar(CLAVE));
    }

    @Test
    @DisplayName("La contraseña correcta se valida")
    void claveCorrectaValida() {
        String guardado = CifradoClave.cifrar(CLAVE);
        assertTrue(CifradoClave.verificar(CLAVE, guardado));
    }

    @Test
    @DisplayName("Una contraseña incorrecta no se valida")
    void claveIncorrectaNoValida() {
        String guardado = CifradoClave.cifrar(CLAVE);
        assertFalse(CifradoClave.verificar("demo2026", guardado));
        assertFalse(CifradoClave.verificar("", guardado));
    }

    @Test
    @DisplayName("Un valor guardado sin cifrar o dañado no permite entrar")
    void valorGuardadoInvalidoNoValida() {
        assertFalse(CifradoClave.verificar(CLAVE, CLAVE));
        assertFalse(CifradoClave.verificar(CLAVE, "pbkdf2$abc$xyz$123"));
        assertFalse(CifradoClave.verificar(CLAVE, null));
        assertFalse(CifradoClave.verificar(null, CifradoClave.cifrar(CLAVE)));
    }

    @Test
    @DisplayName("No se permite cifrar una contraseña vacía")
    void claveVaciaLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> CifradoClave.cifrar(""));
        assertThrows(IllegalArgumentException.class, () -> CifradoClave.cifrar(null));
    }
}
