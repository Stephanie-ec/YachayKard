package pe.edu.utp.yachaykard.servicio;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.AsignacionCursoDAO;
import pe.edu.utp.yachaykard.dao.CompetenciaDAO;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Competencia;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.modelo.ReporteCierre;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pruebas de la exportación a la plantilla RegNotas del SIAGIE (RF-32). Usa la plantilla real
 * de src/main/resources y los DAO simulados en Mockito. Datos ficticios.
 */
class ExportadorSiagieTest {

    private static final int SECCION = 1;
    private static final int PERIODO = 3;
    private static final int MATEMATICA = 1;
    private static final int COMUNICACION = 2;

    private EstudianteDAO estudianteDAO;
    private AsignacionCursoDAO asignacionDAO;
    private ExportadorSiagie exportador;
    private ExportadorSiagie.Cabecera cabecera;
    private final DataFormatter formato = new DataFormatter();

    @BeforeEach
    void preparar() throws SQLException {
        estudianteDAO = mock(EstudianteDAO.class);
        CursoDAO cursoDAO = mock(CursoDAO.class);
        CompetenciaDAO competenciaDAO = mock(CompetenciaDAO.class);
        ReporteCierreDAO reporteDAO = mock(ReporteCierreDAO.class);
        asignacionDAO = mock(AsignacionCursoDAO.class);
        exportador = new ExportadorSiagie(estudianteDAO, cursoDAO, competenciaDAO, reporteDAO, asignacionDAO);

        when(cursoDAO.listarTodos()).thenReturn(List.of(curso(MATEMATICA, "063"), curso(COMUNICACION, "017")));
        when(competenciaDAO.listarPorCurso(MATEMATICA)).thenReturn(List.of(competencia(11, "063-01"), competencia(12, "063-02")));
        when(competenciaDAO.listarPorCurso(COMUNICACION)).thenReturn(List.of(competencia(21, "017-02")));
        when(estudianteDAO.listarPorSeccion(SECCION)).thenReturn(List.of(
                estudiante(4, "71234504", "Castillo Núñez", "Mateo"),
                estudiante(5, "71234505", "Ramos Peña", "Sofía")));
        when(reporteDAO.listarPorEstudiante(4)).thenReturn(List.of(
                nivel(11, PERIODO, "A", "Resuelve operaciones con fracciones."),
                nivel(12, PERIODO, "C", "Necesita reforzar ecuaciones lineales."),
                nivel(11, 1, "B", "Nivel de otro periodo.")));

        Grado grado = new Grado();
        grado.setNumeroGrado("1°");
        Seccion seccion = new Seccion();
        seccion.setIdSeccion(SECCION);
        seccion.setLetra('A');
        Periodo periodo = new Periodo();
        periodo.setIdPeriodo(PERIODO);
        periodo.setNombrePeriodo("III Trimestre");
        periodo.setTipo("Trimestre");
        periodo.setFechaInicio(LocalDate.of(2026, 9, 14));
        cabecera = new ExportadorSiagie.Cabecera("1234567", "I.E. Demo YachayKard", grado, seccion, periodo);
    }

    private static Curso curso(int id, String codigo) {
        Curso c = new Curso();
        c.setIdCurso(id);
        c.setCodigoSiagie(codigo);
        return c;
    }

    private static Competencia competencia(int id, String codigo) {
        Competencia c = new Competencia();
        c.setIdCompetencia(id);
        c.setCodigo(codigo);
        return c;
    }

    private static Estudiante estudiante(int id, String dni, String apellidos, String nombres) {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(id);
        e.setDni(dni);
        e.setApellidos(apellidos);
        e.setNombres(nombres);
        e.setCodigoEstudiante(String.format("%014d", id));
        return e;
    }

    private static ReporteCierre nivel(int idCompetencia, int idPeriodo, String nivel, String conclusion) {
        ReporteCierre r = new ReporteCierre();
        r.setIdEstudiante(4);
        r.setIdCompetencia(idCompetencia);
        r.setIdPeriodo(idPeriodo);
        r.setNivelFinal(nivel);
        r.setConclusionDescriptiva(conclusion);
        return r;
    }

    private static AsignacionCurso asignacion(int idCurso, int idSeccion) {
        AsignacionCurso a = new AsignacionCurso();
        a.setIdCurso(idCurso);
        a.setIdSeccion(idSeccion);
        return a;
    }

    private static Workbook abrir(ExportadorSiagie.Exportacion exportacion) throws IOException {
        return new XSSFWorkbook(new ByteArrayInputStream(exportacion.getContenido()));
    }

    /** Texto de una celda (fila y columna empiezan en 0); vacío si no existe. */
    private String texto(Sheet hoja, int fila, int columna) {
        Row r = hoja.getRow(fila);
        Cell c = r == null ? null : r.getCell(columna);
        return c == null ? "" : formato.formatCellValue(c);
    }

    @Test
    @DisplayName("Cada hoja de área tiene una fila por estudiante y borra las filas de ejemplo")
    void unaFilaPorEstudiante() throws Exception {
        try (Workbook libro = abrir(exportador.exportar(cabecera, null))) {
            Sheet mate = libro.getSheet("063-MATE");
            assertEquals("71234504", texto(mate, 2, 0));
            assertEquals("00000000000004", texto(mate, 2, 1));
            assertEquals("CASTILLO NÚÑEZ, MATEO", texto(mate, 2, 2));
            assertEquals("RAMOS PEÑA, SOFÍA", texto(mate, 3, 2));
            assertEquals("", texto(mate, 4, 0));
            assertEquals("RAMOS PEÑA, SOFÍA", texto(libro.getSheet("0001-ART Y CULT"), 3, 2));
        }
    }

    @Test
    @DisplayName("El nivel final y la conclusión del periodo van en la columna de su competencia")
    void nivelesEnSuColumna() throws Exception {
        ExportadorSiagie.Exportacion exportacion = exportador.exportar(cabecera, null);
        try (Workbook libro = abrir(exportacion)) {
            Sheet mate = libro.getSheet("063-MATE");
            assertEquals("A", texto(mate, 2, 3));
            assertEquals("Resuelve operaciones con fracciones.", texto(mate, 2, 4));
            assertEquals("C", texto(mate, 2, 5));
            assertEquals("", texto(mate, 3, 3));
            assertEquals("", texto(libro.getSheet("017-COMU"), 2, 5));
        }
        assertEquals(1, exportacion.getHojasConNiveles());
    }

    @Test
    @DisplayName("Generalidades y Parametros llevan la institución, el periodo, el grado y la sección")
    void cabecera() throws Exception {
        try (Workbook libro = abrir(exportador.exportar(cabecera, null))) {
            Sheet generalidades = libro.getSheet("Generalidades");
            assertEquals("1234567-0", texto(generalidades, 4, 4));
            assertEquals("I.E. DEMO YACHAYKARD", texto(generalidades, 5, 2));
            assertEquals("2026", texto(generalidades, 7, 3));
            assertEquals("TERCER TRIMESTRE", texto(generalidades, 9, 3));
            assertEquals("PRIMERO", texto(generalidades, 9, 7));
            assertEquals("A", texto(generalidades, 9, 9));
            Sheet parametros = libro.getSheet("Parametros");
            assertEquals("T3", texto(parametros, 5, 1));
            assertEquals("10", texto(parametros, 6, 1));
            assertEquals("01", texto(parametros, 7, 1));
        }
    }

    @Test
    @DisplayName("El docente solo recibe las hojas de sus cursos en esa sección")
    void docenteSoloSusHojas() throws Exception {
        when(asignacionDAO.listarPorDocente(7)).thenReturn(List.of(
                asignacion(MATEMATICA, SECCION), asignacion(COMUNICACION, 2)));

        try (Workbook libro = abrir(exportador.exportar(cabecera, 7))) {
            assertEquals(3, libro.getNumberOfSheets());
            assertEquals(-1, libro.getSheetIndex("017-COMU"));
            assertEquals("A", texto(libro.getSheet("063-MATE"), 2, 3));
        }
    }

    @Test
    @DisplayName("Sin cursos en la sección o sin estudiantes no se exporta")
    void sinCursosNiEstudiantes() throws SQLException {
        when(asignacionDAO.listarPorDocente(8)).thenReturn(List.of(asignacion(MATEMATICA, 2)));
        assertThrows(IllegalStateException.class, () -> exportador.exportar(cabecera, 8));

        when(estudianteDAO.listarPorSeccion(SECCION)).thenReturn(List.of());
        assertThrows(IllegalStateException.class, () -> exportador.exportar(cabecera, null));
    }

    @Test
    @DisplayName("Con más de 23 estudiantes se agregan filas y la leyenda baja")
    void masEstudiantesQueFilas() throws Exception {
        List<Estudiante> muchos = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            muchos.add(estudiante(100 + i, String.valueOf(70000000 + i), "Apellido " + i, "Nombre"));
        }
        when(estudianteDAO.listarPorSeccion(SECCION)).thenReturn(muchos);

        try (Workbook libro = abrir(exportador.exportar(cabecera, null))) {
            Sheet mate = libro.getSheet("063-MATE");
            assertEquals("70000025", texto(mate, 26, 0));
            assertEquals("LEYENDA", texto(mate, 29, 1));
        }
    }
}