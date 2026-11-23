package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.modelo.Apoderado;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de los datos del apoderado (RF-11). Datos ficticios.
 */
class ValidadorApoderadoTest {

    private final ValidadorApoderado validador = new ValidadorApoderado();
    private Apoderado apoderado;

    @BeforeEach
    void preparar() {
        apoderado = new Apoderado();
        apoderado.setDni("10456789");
        apoderado.setNombres("Carmen Núñez Torres");
        apoderado.setTelefono("987654321");
    }

    private static Apoderado existente(int id, String dni) {
        Apoderado a = new Apoderado();
        a.setIdApoderado(id);
        a.setDni(dni);
        return a;
    }

    @Test
    @DisplayName("Un apoderado con datos correctos no tiene errores")
    void datosCorrectos() {
        assertTrue(validador.validar(apoderado, List.of()).isEmpty());
    }

    @Test
    @DisplayName("El celular es opcional")
    void celularOpcional() {
        apoderado.setTelefono("");
        assertTrue(validador.validar(apoderado, List.of()).isEmpty());
    }

    @Test
    @DisplayName("El celular debe tener 9 dígitos y empezar con 9")
    void celularInvalido() {
        apoderado.setTelefono("012345678");
        assertEquals(List.of("El celular debe tener 9 dígitos y empezar con 9."), validador.validar(apoderado, List.of()));
        apoderado.setTelefono("98765432");
        assertEquals(1, validador.validar(apoderado, List.of()).size());
    }

    @Test
    @DisplayName("No se repite el DNI de otro apoderado")
    void dniRepetido() {
        assertEquals(List.of("Ya existe un apoderado con ese DNI."),
                validador.validar(apoderado, List.of(existente(3, "10456789"))));
    }

    @Test
    @DisplayName("Al editar, su propio DNI no cuenta como repetido")
    void dniPropioAlEditar() {
        apoderado.setIdApoderado(3);
        assertTrue(validador.validar(apoderado, List.of(existente(3, "10456789"))).isEmpty());
    }

    @Test
    @DisplayName("DNI y nombres son obligatorios")
    void obligatorios() {
        apoderado.setDni("123");
        apoderado.setNombres(" ");
        assertEquals(2, validador.validar(apoderado, List.of()).size());
    }
}