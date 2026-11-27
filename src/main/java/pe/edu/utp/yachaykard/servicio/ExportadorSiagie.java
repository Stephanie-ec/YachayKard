package pe.edu.utp.yachaykard.servicio;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.edu.utp.yachaykard.dao.AsignacionCursoDAO;
import pe.edu.utp.yachaykard.dao.CompetenciaDAO;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.EstudianteDAO;
import pe.edu.utp.yachaykard.dao.ReporteCierreDAO;
import pe.edu.utp.yachaykard.dao.impl.AsignacionCursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CompetenciaDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.CursoDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.EstudianteDAOImpl;
import pe.edu.utp.yachaykard.dao.impl.ReporteCierreDAOImpl;
import pe.edu.utp.yachaykard.modelo.AsignacionCurso;
import pe.edu.utp.yachaykard.modelo.Competencia;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Estudiante;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Periodo;
import pe.edu.utp.yachaykard.modelo.ReporteCierre;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exportación a la plantilla RegNotas del SIAGIE con Apache POI (RF-32).
 * Parte de la plantilla vacía (src/main/resources/plantillas) y genera un libro por sección y periodo:
 * <ul>
 *     <li>Generalidades y Parametros: código modular, institución, año, periodo, grado y sección.</li>
 *     <li>En cada hoja de área: DNI, código de estudiante y nombres de los estudiantes de la sección.</li>
 *     <li>Por competencia: nivel final (NL) y conclusión descriptiva del cierre del periodo (RF-30).</li>
 * </ul>
 * El administrador recibe todas las hojas; el docente, solo las de sus cursos en esa sección.
 * Los DAO se reciben en el constructor para poder simularlos con Mockito en las pruebas.
 */
public class ExportadorSiagie {

    /** Plantilla vacía dentro de src/main/resources. */
    public static final String PLANTILLA = "/plantillas/RegNotas_plantilla.xlsx";
    /** Fila 3 de Excel: primera fila de estudiantes. */
    static final int PRIMERA_FILA = 2;
    /** La plantilla trae 23 filas para estudiantes (filas 3 a 25). */
    static final int FILAS_PLANTILLA = 23;

    private final EstudianteDAO estudianteDAO;
    private final CursoDAO cursoDAO;
    private final CompetenciaDAO competenciaDAO;
    private final ReporteCierreDAO reporteDAO;
    private final AsignacionCursoDAO asignacionDAO;

    /** Datos de la cabecera del libro. */
    public record Cabecera(String codigoModular, String institucion, Grado grado, Seccion seccion, Periodo periodo) {
    }

    /** Resultado: el archivo y cuántas hojas recibieron niveles finales. */
    public static final class Exportacion {
        private final byte[] contenido;
        private final int hojasConNiveles;

        Exportacion(byte[] contenido, int hojasConNiveles) {
            this.contenido = contenido;
            this.hojasConNiveles = hojasConNiveles;
        }

        public byte[] getContenido() {
            return contenido.clone();
        }

        public int getHojasConNiveles() {
            return hojasConNiveles;
        }
    }

    /** Constructor que usa la aplicación: trabaja con la base de datos. */
    public ExportadorSiagie() {
        this(new EstudianteDAOImpl(), new CursoDAOImpl(), new CompetenciaDAOImpl(),
                new ReporteCierreDAOImpl(), new AsignacionCursoDAOImpl());
    }

    /**
     * Constructor para pruebas.
     *
     * @param estudianteDAO  acceso a estudiantes
     * @param cursoDAO       acceso a cursos (áreas)
     * @param competenciaDAO acceso a competencias
     * @param reporteDAO     acceso a los niveles finales del cierre
     * @param asignacionDAO  acceso a las asignaciones del docente
     */
    public ExportadorSiagie(EstudianteDAO estudianteDAO, CursoDAO cursoDAO, CompetenciaDAO competenciaDAO,
                            ReporteCierreDAO reporteDAO, AsignacionCursoDAO asignacionDAO) {
        this.estudianteDAO = estudianteDAO;
        this.cursoDAO = cursoDAO;
        this.competenciaDAO = competenciaDAO;
        this.reporteDAO = reporteDAO;
        this.asignacionDAO = asignacionDAO;
    }

    /**
     * Genera el libro RegNotas de una sección y un periodo.
     *
     * @param cabecera  institución, grado, sección y periodo
     * @param idDocente docente que exporta; null si es el administrador (recibe todas las hojas)
     * @return archivo .xlsx y cantidad de hojas con niveles
     * @throws IllegalStateException si el docente no tiene cursos en la sección o no hay estudiantes
     * @throws SQLException          si ocurre un error en la base de datos
     * @throws IOException           si no se puede leer la plantilla
     */
    public Exportacion exportar(Cabecera cabecera, Integer idDocente) throws SQLException, IOException {
        int idSeccion = cabecera.seccion().getIdSeccion();
        Map<String, Curso> cursos = cursoDAO.listarTodos().stream()
                .filter(c -> c.getCodigoSiagie() != null)
                .collect(Collectors.toMap(Curso::getCodigoSiagie, c -> c, (a, b) -> a));
        Set<String> permitidos = idDocente == null ? null : codigosDelDocente(idDocente, idSeccion, cursos);
        List<Estudiante> estudiantes = estudianteDAO.listarPorSeccion(idSeccion);
        if (estudiantes.isEmpty()) {
            throw new IllegalStateException("La sección no tiene estudiantes con matrícula activa.");
        }
        Map<Integer, Map<Integer, ReporteCierre>> niveles = nivelesFinales(estudiantes, cabecera.periodo().getIdPeriodo());

        try (InputStream plantilla = ExportadorSiagie.class.getResourceAsStream(PLANTILLA)) {
            if (plantilla == null) {
                throw new IOException("No se encontró la plantilla " + PLANTILLA + ".");
            }
            try (Workbook libro = new XSSFWorkbook(plantilla);
                 ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
                llenarCabecera(libro, cabecera);
                int hojasConNiveles = 0;
                for (int i = libro.getNumberOfSheets() - 1; i >= 0; i--) {
                    String codigo = CodigosSiagie.codigoHoja(libro.getSheetName(i));
                    if (codigo == null) {
                        continue;
                    }
                    if (permitidos != null && !permitidos.contains(codigo)) {
                        libro.removeSheetAt(i);
                    } else if (llenarHoja(libro.getSheetAt(i), estudiantes, cursos.get(codigo), niveles)) {
                        hojasConNiveles++;
                    }
                }
                libro.setActiveSheet(0);
                libro.write(salida);
                return new Exportacion(salida.toByteArray(), hojasConNiveles);
            }
        }
    }

    /**
     * @return códigos de las áreas que el docente dicta en la sección
     * @throws IllegalStateException si no tiene cursos en la sección
     */
    private Set<String> codigosDelDocente(int idDocente, int idSeccion, Map<String, Curso> cursos) throws SQLException {
        Set<Integer> idsCurso = asignacionDAO.listarPorDocente(idDocente).stream()
                .filter(a -> a.getIdSeccion() == idSeccion)
                .map(AsignacionCurso::getIdCurso)
                .collect(Collectors.toSet());
        Set<String> codigos = cursos.values().stream()
                .filter(c -> idsCurso.contains(c.getIdCurso()))
                .map(Curso::getCodigoSiagie)
                .collect(Collectors.toSet());
        if (codigos.isEmpty()) {
            throw new IllegalStateException("No tiene cursos asignados en esta sección.");
        }
        return codigos;
    }

    /**
     * @return por estudiante, los niveles finales del periodo por competencia
     */
    private Map<Integer, Map<Integer, ReporteCierre>> nivelesFinales(List<Estudiante> estudiantes, int idPeriodo)
            throws SQLException {
        Map<Integer, Map<Integer, ReporteCierre>> niveles = new HashMap<>();
        for (Estudiante e : estudiantes) {
            Map<Integer, ReporteCierre> porCompetencia = new HashMap<>();
            for (ReporteCierre r : reporteDAO.listarPorEstudiante(e.getIdEstudiante())) {
                if (r.getIdPeriodo() == idPeriodo && r.getNivelFinal() != null) {
                    porCompetencia.put(r.getIdCompetencia(), r);
                }
            }
            niveles.put(e.getIdEstudiante(), porCompetencia);
        }
        return niveles;
    }

    /** Llena las hojas Generalidades y Parametros. */
    private void llenarCabecera(Workbook libro, Cabecera c) {
        Periodo periodo = c.periodo();
        String institucion = c.institucion().toUpperCase(Locale.ROOT);
        String grado = c.grado().getNumeroGrado();
        String letra = String.valueOf(c.seccion().getLetra()).toUpperCase(Locale.ROOT);
        int anio = periodo.getFechaInicio().getYear();

        Sheet generalidades = libro.getSheet("Generalidades");
        if (generalidades != null) {
            escribir(generalidades, "E5", c.codigoModular() + "-0");
            escribir(generalidades, "C6", institucion);
            escribirNumero(generalidades, "D8", anio);
            escribir(generalidades, "D10", CodigosSiagie.nombrePeriodo(periodo));
            escribir(generalidades, "H10", CodigosSiagie.nombreGrado(grado));
            escribir(generalidades, "J10", letra);
        }
        Sheet parametros = libro.getSheet("Parametros");
        if (parametros != null) {
            escribir(parametros, "B1", c.codigoModular());
            escribir(parametros, "C1", institucion);
            escribirNumero(parametros, "B4", anio);
            escribir(parametros, "B6", CodigosSiagie.codigoPeriodo(periodo));
            escribir(parametros, "C6", CodigosSiagie.nombrePeriodo(periodo));
            escribir(parametros, "B7", CodigosSiagie.codigoGrado(grado));
            escribir(parametros, "C7", CodigosSiagie.nombreGrado(grado));
            escribir(parametros, "B8", CodigosSiagie.codigoSeccion(c.seccion().getLetra()));
            escribir(parametros, "C8", letra);
        }
    }

    /**
     * Escribe la lista de estudiantes en una hoja de área y, si el área tiene niveles finales,
     * su NL y su conclusión descriptiva.
     *
     * @return true si escribió al menos un nivel
     */
    private boolean llenarHoja(Sheet hoja, List<Estudiante> estudiantes, Curso curso,
                               Map<Integer, Map<Integer, ReporteCierre>> niveles) throws SQLException {
        int ultimaColumna = hoja.getRow(1) != null ? hoja.getRow(1).getLastCellNum() : 3;
        prepararFilas(hoja, estudiantes.size(), ultimaColumna);
        List<Competencia> competencias = curso != null ? competenciaDAO.listarPorCurso(curso.getIdCurso()) : List.of();
        boolean conNiveles = false;
        int filas = Math.max(estudiantes.size(), FILAS_PLANTILLA);
        for (int r = 0; r < filas; r++) {
            Row fila = fila(hoja, PRIMERA_FILA + r);
            for (int col = 0; col < ultimaColumna; col++) {
                escribir(fila, col, null);
            }
            if (r >= estudiantes.size()) {
                continue;
            }
            Estudiante e = estudiantes.get(r);
            escribir(fila, 0, e.getDni());
            escribir(fila, 1, e.getCodigoEstudiante());
            escribir(fila, 2, CodigosSiagie.nombreEstudiante(e));
            for (Competencia comp : competencias) {
                int columna = CodigosSiagie.columnaNivel(comp.getCodigo());
                ReporteCierre nivel = niveles.getOrDefault(e.getIdEstudiante(), Map.of()).get(comp.getIdCompetencia());
                if (columna >= 0 && columna + 1 < ultimaColumna && nivel != null) {
                    escribir(fila, columna, nivel.getNivelFinal());
                    escribir(fila, columna + 1, nivel.getConclusionDescriptiva());
                    conNiveles = true;
                }
            }
        }
        return conNiveles;
    }

    /**
     * Si hay más estudiantes que filas en la plantilla, baja la leyenda y copia el formato de la fila 3.
     */
    private void prepararFilas(Sheet hoja, int estudiantes, int ultimaColumna) {
        int extra = estudiantes - FILAS_PLANTILLA;
        int primeraLibre = PRIMERA_FILA + FILAS_PLANTILLA;
        if (extra <= 0) {
            return;
        }
        if (hoja.getLastRowNum() >= primeraLibre) {
            hoja.shiftRows(primeraLibre, hoja.getLastRowNum(), extra);
        }
        Row modelo = hoja.getRow(PRIMERA_FILA);
        for (int r = primeraLibre; r < primeraLibre + extra; r++) {
            Row nueva = fila(hoja, r);
            for (int col = 0; col < ultimaColumna && modelo != null; col++) {
                Cell origen = modelo.getCell(col);
                if (origen != null) {
                    celda(nueva, col).setCellStyle(origen.getCellStyle());
                }
            }
        }
    }

    private static Row fila(Sheet hoja, int indice) {
        Row fila = hoja.getRow(indice);
        return fila != null ? fila : hoja.createRow(indice);
    }

    private static Cell celda(Row fila, int columna) {
        Cell celda = fila.getCell(columna);
        return celda != null ? celda : fila.createCell(columna);
    }

    /**
     * Escribe un texto en una celda; si el texto es null, la deja vacía.
     * Primero vacía la celda: la plantilla guarda textos como "inlineStr" y POI no los reemplaza
     * si se escribe encima.
     */
    private static void escribir(Row fila, int columna, String valor) {
        Cell existente = fila.getCell(columna);
        if (existente != null) {
            existente.setBlank();
        }
        if (valor != null) {
            celda(fila, columna).setCellValue(valor);
        }
    }

    private static void escribir(Sheet hoja, String referencia, String valor) {
        CellReference ref = new CellReference(referencia);
        escribir(fila(hoja, ref.getRow()), ref.getCol(), valor);
    }

    private static void escribirNumero(Sheet hoja, String referencia, int valor) {
        CellReference ref = new CellReference(referencia);
        celda(fila(hoja, ref.getRow()), ref.getCol()).setCellValue(valor);
    }
}