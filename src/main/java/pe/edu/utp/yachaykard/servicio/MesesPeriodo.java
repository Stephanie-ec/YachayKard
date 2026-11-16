package pe.edu.utp.yachaykard.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cálculo de los meses que abarca un periodo académico (bimestre o trimestre).
 * Las calificaciones se registran por mes, así que la pantalla solo ofrece
 * los meses que caen dentro del periodo elegido.
 */
public final class MesesPeriodo {

    private MesesPeriodo() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Meses (1 a 12) que tocan el periodo, en orden, incluido el mes de inicio y el de fin.
     *
     * @param inicio fecha de inicio del periodo
     * @param fin    fecha de fin del periodo
     * @return números de mes; lista vacía si falta una fecha o si el fin es anterior al inicio
     */
    public static List<Integer> meses(LocalDate inicio, LocalDate fin) {
        List<Integer> meses = new ArrayList<>();
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            return meses;
        }
        LocalDate cursor = inicio.withDayOfMonth(1);
        while (!cursor.isAfter(fin)) {
            meses.add(cursor.getMonthValue());
            cursor = cursor.plusMonths(1);
        }
        return meses;
    }

    /**
     * Mes que se preselecciona en la pantalla: el mes actual si está dentro del periodo;
     * si no, el primer mes del periodo.
     *
     * @param meses     meses del periodo
     * @param mesActual mes de hoy (1 a 12)
     * @return mes a preseleccionar, o 0 si el periodo no tiene meses
     */
    public static int mesPorDefecto(List<Integer> meses, int mesActual) {
        if (meses.contains(mesActual)) {
            return mesActual;
        }
        return meses.isEmpty() ? 0 : meses.get(0);
    }
}