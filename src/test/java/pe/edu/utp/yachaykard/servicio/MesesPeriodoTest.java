package pe.edu.utp.yachaykard.servicio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del cálculo de meses de un periodo (pantalla Calificaciones).
 */
class MesesPeriodoTest {

    @Test
    @DisplayName("Un trimestre de marzo a mayo tiene los meses 3, 4 y 5")
    void trimestreCompleto() {
        assertEquals(List.of(3, 4, 5),
                MesesPeriodo.meses(LocalDate.of(2026, 3, 9), LocalDate.of(2026, 5, 15)));
    }

    @Test
    @DisplayName("Un periodo que empieza a fin de mes incluye ese mes")
    void incluyeElMesDeInicioAunqueEmpieceTarde() {
        assertEquals(List.of(7, 8),
                MesesPeriodo.meses(LocalDate.of(2026, 7, 31), LocalDate.of(2026, 8, 1)));
    }

    @Test
    @DisplayName("Un periodo dentro de un solo mes tiene un mes")
    void unSoloMes() {
        assertEquals(List.of(10),
                MesesPeriodo.meses(LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 30)));
    }

    @Test
    @DisplayName("Sin fechas o con fechas invertidas no hay meses")
    void fechasInvalidas() {
        assertTrue(MesesPeriodo.meses(null, LocalDate.of(2026, 5, 1)).isEmpty());
        assertTrue(MesesPeriodo.meses(LocalDate.of(2026, 5, 1), null).isEmpty());
        assertTrue(MesesPeriodo.meses(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 5, 1)).isEmpty());
    }

    @Test
    @DisplayName("Si el mes actual está en el periodo, se preselecciona")
    void preseleccionaMesActual() {
        assertEquals(4, MesesPeriodo.mesPorDefecto(List.of(3, 4, 5), 4));
    }

    @Test
    @DisplayName("Si el mes actual está fuera del periodo, se preselecciona el primero")
    void preseleccionaPrimerMes() {
        assertEquals(3, MesesPeriodo.mesPorDefecto(List.of(3, 4, 5), 11));
    }

    @Test
    @DisplayName("Sin meses no se preselecciona nada")
    void sinMeses() {
        assertEquals(0, MesesPeriodo.mesPorDefecto(List.of(), 4));
    }
}