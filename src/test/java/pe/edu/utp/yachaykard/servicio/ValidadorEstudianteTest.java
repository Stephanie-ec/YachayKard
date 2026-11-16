package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.util.Zona;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de las reglas de datos del estudiante (RF-10). Usan datos ficticios.
 */
class ValidadorEstudianteTest {

    private final ValidadorEstudiante validador = new ValidadorEstudiante();
    private Estudiante estudiante;

    @BeforeEach
    void preparar() {
        estudiante = new Estudiante();
        estudiante.setDni("71234504");
        estudiante.setNombres("Mateo");
        estudiante.setApellidos("Castillo Núñez");
        estudiante.setCodigoEstudiante("12345678901234");
        estudiante.setFechaNacimiento(LocalDate.of(2013, 5, 20));
        estudiante.setIdApoderado(1);
    }

    @Test
    @DisplayName("Un estudiante con datos correctos no tiene errores")
    void datosCorrectos() {
        assertTrue(validador.validar(estudiante).isEmpty());
    }

    @Test
    @DisplayName("El DNI debe tener exactamente 8 dígitos")
    void dniDeOchoDigitos() {
        assertTrue(validador.dniValido("71234504"));
        assertFalse(validador.dniValido("7123450"));
        assertFalse(validador.dniValido("712345041"));
        assertFalse(validador.dniValido("7123450A"));
        assertFalse(validador.dniValido(null));
    }

    @Test
    @DisplayName("El código de estudiante debe tener exactamente 14 dígitos")
    void codigoDeCatorceDigitos() {
        assertTrue(validador.codigoValido("12345678901234"));
        assertFalse(validador.codigoValido("1234567890123"));
        assertFalse(validador.codigoValido("1234567890123X"));
    }

    @Test
    @DisplayName("El código de estudiante es opcional")
    void codigoOpcional() {
        estudiante.setCodigoEstudiante("");
        assertTrue(validador.validar(estudiante).isEmpty());
        estudiante.setCodigoEstudiante(null);
        assertTrue(validador.validar(estudiante).isEmpty());
    }

    @Test
    @DisplayName("Nombres y apellidos en blanco son errores")
    void nombresYApellidosObligatorios() {
        estudiante.setNombres("   ");
        estudiante.setApellidos(null);

        List<String> errores = validador.validar(estudiante);

        assertEquals(2, errores.size());
        assertTrue(errores.contains("Los nombres son obligatorios."));
        assertTrue(errores.contains("Los apellidos son obligatorios."));
    }

    @Test
    @DisplayName("La fecha de nacimiento no puede ser futura")
    void fechaNacimientoFutura() {
        estudiante.setFechaNacimiento(Zona.hoy().plusDays(1));
        assertEquals(List.of("La fecha de nacimiento no puede ser posterior a hoy."), validador.validar(estudiante));
    }

    @Test
    @DisplayName("Sin apoderado no se puede registrar")
    void apoderadoObligatorio() {
        estudiante.setIdApoderado(0);
        assertEquals(List.of("Seleccione un apoderado."), validador.validar(estudiante));
    }

    @Test
    @DisplayName("Se informan todos los errores a la vez")
    void variosErrores() {
        estudiante.setDni("123");
        estudiante.setCodigoEstudiante("999");
        assertEquals(2, validador.validar(estudiante).size());
    }
}
