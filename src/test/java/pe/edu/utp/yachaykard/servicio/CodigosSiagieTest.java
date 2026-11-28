package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Periodo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de los códigos de la plantilla RegNotas del SIAGIE (RF-32).
 */
class CodigosSiagieTest {

    private static Periodo periodo(String nombre, String tipo) {
        Periodo p = new Periodo();
        p.setNombrePeriodo(nombre);
        p.setTipo(tipo);
        return p;
    }

    @Test
    @DisplayName("El grado se escribe en letras y con el código de la hoja Parametros")
    void grado() {
        assertEquals("CUARTO", CodigosSiagie.nombreGrado("4°"));
        assertEquals("13", CodigosSiagie.codigoGrado("4°"));
        assertEquals("PRIMERO", CodigosSiagie.nombreGrado("1°"));
        assertEquals("10", CodigosSiagie.codigoGrado("1°"));
        assertThrows(IllegalArgumentException.class, () -> CodigosSiagie.nombreGrado("6°"));
    }

    @Test
    @DisplayName("La sección A es 01 y la C es 03")
    void seccion() {
        assertEquals("01", CodigosSiagie.codigoSeccion('A'));
        assertEquals("03", CodigosSiagie.codigoSeccion('c'));
    }

    @Test
    @DisplayName("El periodo se escribe como en el SIAGIE: SEGUNDO TRIMESTRE y T2")
    void periodoTrimestre() {
        Periodo segundo = periodo("II Trimestre", "Trimestre");
        assertEquals("SEGUNDO TRIMESTRE", CodigosSiagie.nombrePeriodo(segundo));
        assertEquals("T2", CodigosSiagie.codigoPeriodo(segundo));
        assertEquals("TERCER TRIMESTRE", CodigosSiagie.nombrePeriodo(periodo("III Trimestre", "Trimestre")));
    }

    @Test
    @DisplayName("Los bimestres usan B1 a B4")
    void periodoBimestre() {
        Periodo cuarto = periodo("IV Bimestre", "Bimestre");
        assertEquals("CUARTO BIMESTRE", CodigosSiagie.nombrePeriodo(cuarto));
        assertEquals("B4", CodigosSiagie.codigoPeriodo(cuarto));
        assertThrows(IllegalArgumentException.class, () -> CodigosSiagie.codigoPeriodo(periodo("Primer trimestre", "Trimestre")));
    }

    @Test
    @DisplayName("Solo las hojas de área tienen código")
    void codigoHoja() {
        assertEquals("063", CodigosSiagie.codigoHoja("063-MATE"));
        assertEquals("0001", CodigosSiagie.codigoHoja("0001-ART Y CULT"));
        assertNull(CodigosSiagie.codigoHoja("Generalidades"));
        assertNull(CodigosSiagie.codigoHoja("Parametros"));
    }

    @Test
    @DisplayName("La competencia 01 va en la columna D y la 04 en la J")
    void columnaNivel() {
        assertEquals(3, CodigosSiagie.columnaNivel("063-01"));
        assertEquals(9, CodigosSiagie.columnaNivel("063-04"));
        assertEquals(-1, CodigosSiagie.columnaNivel("MAT"));
        assertEquals(-1, CodigosSiagie.columnaNivel("063-XX"));
    }

    @Test
    @DisplayName("El nombre va en mayúsculas como en la nómina")
    void nombre() {
        Estudiante e = new Estudiante();
        e.setApellidos("Castillo Núñez");
        e.setNombres("Mateo");
        assertEquals("CASTILLO NÚÑEZ, MATEO", CodigosSiagie.nombreEstudiante(e));
    }
}