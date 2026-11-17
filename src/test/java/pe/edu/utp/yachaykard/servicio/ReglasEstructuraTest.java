package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.utp.yachaykard.dao.CursoDAO;
import pe.edu.utp.yachaykard.dao.GradoDAO;
import pe.edu.utp.yachaykard.dao.SeccionDAO;
import pe.edu.utp.yachaykard.modelo.Curso;
import pe.edu.utp.yachaykard.modelo.Grado;
import pe.edu.utp.yachaykard.modelo.Seccion;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de grados, secciones y cursos (RF-05, RF-06, RF-09),
 * con los DAO simulados en Mockito.
 */
class ReglasEstructuraTest {

    private GradoDAO gradoDAO;
    private SeccionDAO seccionDAO;
    private CursoDAO cursoDAO;
    private ReglasEstructura reglas;

    @BeforeEach
    void preparar() throws SQLException {
        gradoDAO = mock(GradoDAO.class);
        seccionDAO = mock(SeccionDAO.class);
        cursoDAO = mock(CursoDAO.class);
        reglas = new ReglasEstructura(gradoDAO, seccionDAO, cursoDAO);

        Grado primero = new Grado();
        primero.setIdGrado(1);
        primero.setNumeroGrado("1°");
        primero.setNivel("Secundaria");
        when(gradoDAO.listarTodos()).thenReturn(List.of(primero));
        when(seccionDAO.listarTodos()).thenReturn(List.of(seccion(1, 1, 'A', 30)));
        Curso mate = new Curso();
        mate.setIdCurso(1);
        mate.setNombreCurso("Matemática");
        when(cursoDAO.listarTodos()).thenReturn(List.of(mate));
    }

    private static Seccion seccion(int id, int idGrado, char letra, int aforo) {
        Seccion s = new Seccion();
        s.setIdSeccion(id);
        s.setIdGrado(idGrado);
        s.setLetra(letra);
        s.setAforo(aforo);
        return s;
    }

    @Test
    @DisplayName("Un grado nuevo se registra; uno repetido no")
    void grados() throws SQLException {
        Grado segundo = new Grado();
        segundo.setNumeroGrado("2°");
        segundo.setNivel("Secundaria");
        reglas.guardarGrado(segundo);
        verify(gradoDAO).insertar(segundo);

        Grado repetido = new Grado();
        repetido.setNumeroGrado("1°");
        repetido.setNivel("secundaria");
        assertThrows(IllegalArgumentException.class, () -> reglas.guardarGrado(repetido));
    }

    @Test
    @DisplayName("Una sección nueva se registra con la letra en mayúscula y sin tutor")
    void seccionNueva() throws SQLException {
        Seccion nueva = seccion(0, 1, 'b', 30);
        nueva.setIdDocenteTutor(0);

        reglas.guardarSeccion(nueva);

        assertEquals('B', nueva.getLetra());
        assertNull(nueva.getIdDocenteTutor());
        verify(seccionDAO).insertar(nueva);
    }

    @Test
    @DisplayName("No se repite la letra de una sección en el mismo grado")
    void seccionRepetida() throws SQLException {
        // 1. Extraer la creación del objeto fuera de la lambda
        Seccion nuevaSeccion = seccion(0, 1, 'a', 30);

        // 2. La lambda ahora solo contiene una única llamada
        assertThrows(IllegalArgumentException.class, () -> reglas.guardarSeccion(nuevaSeccion));

        // 3. Tu verificación de Mockito se queda igual
        verify(seccionDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Editar una sección sin cambiar su letra no es repetir")
    void editarSeccion() throws SQLException {
        Seccion misma = seccion(1, 1, 'A', 35);
        reglas.guardarSeccion(misma);
        verify(seccionDAO).actualizar(misma);
    }

    @Test
    @DisplayName("El aforo va de 1 a 45 y la letra de la A a la Z")
    void aforoYLetra() {
        Seccion seccionInvalida1 = seccion(0, 1, 'C', 0);
        assertThrows(IllegalArgumentException.class, () -> reglas.guardarSeccion(seccionInvalida1));

        Seccion seccionInvalida2 = seccion(0, 1, 'C', 46);
        assertThrows(IllegalArgumentException.class, () -> reglas.guardarSeccion(seccionInvalida2));

        Seccion seccionInvalida3 = seccion(0, 1, '1', 30);
        assertThrows(IllegalArgumentException.class, () -> reglas.guardarSeccion(seccionInvalida3));
    }

    @Test
    @DisplayName("No se repite el nombre de un curso, sin importar mayúsculas")
    void cursoRepetido() throws SQLException {
        Curso repetido = new Curso();
        repetido.setNombreCurso(" matemática ");
        repetido.setHorasSemanales(5);

        assertThrows(IllegalArgumentException.class, () -> reglas.guardarCurso(repetido));
        verify(cursoDAO, never()).insertar(any());
    }

    @Test
    @DisplayName("Un curso nuevo válido se registra; las horas van de 0 a 10")
    void cursoNuevo() throws SQLException {
        Curso arte = new Curso();
        arte.setNombreCurso("Arte y Cultura");
        arte.setHorasSemanales(2);
        arte.setCodigoSiagie("");

        reglas.guardarCurso(arte);

        assertNull(arte.getCodigoSiagie());
        verify(cursoDAO).insertar(arte);

        Curso exceso = new Curso();
        exceso.setNombreCurso("Taller");
        exceso.setHorasSemanales(11);
        assertThrows(IllegalArgumentException.class, () -> reglas.guardarCurso(exceso));
    }
}