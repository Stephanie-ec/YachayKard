package pe.edu.utp.yachaykard.servicio;

import java.util.List;

/**
 * Servicio que redacta la conclusión descriptiva de una calificación a partir del nivel de logro,
 * la competencia, la fortaleza y la debilidad (capa Servicios del diagrama de componentes).
 * Según la norma del Minedu, la conclusión es obligatoria para los niveles B y C.
 */
public class GeneradorConclusion {

    /** Frases predefinidas de fortaleza (no hay lista oficial; cada una cabe en VARCHAR(150)). */
    public static final List<String> FORTALEZAS = List.of(
            "Participa activamente en clase",
            "Trabaja bien en equipo",
            "Razona con claridad",
            "Entrega sus trabajos a tiempo");

    /** Frases predefinidas de debilidad (cada una cabe en VARCHAR(150)). */
    public static final List<String> DEBILIDADES = List.of(
            "Dificultad para argumentar sus respuestas",
            "Requiere reforzar los procedimientos",
            "Se distrae con facilidad",
            "No completa sus tareas");

    /**
     * Indica si el nivel exige elegir una debilidad (niveles B y C).
     *
     * @param nivel nivel de logro: AD, A, B o C
     * @return true si el nivel es B o C
     */
    public boolean requiereDebilidad(String nivel) {
        return "B".equals(nivel) || "C".equals(nivel);
    }

    /**
     * Redacta la conclusión descriptiva.
     * <ul>
     *   <li>B o C: "El estudiante alcanzó el nivel {nivel} en {competencia}. Fortaleza: {fortaleza}. A mejorar: {debilidad}."</li>
     *   <li>A o AD: "El estudiante alcanzó el nivel {nivel} en {competencia}. Fortaleza: {fortaleza}."</li>
     * </ul>
     * Si no se eligió fortaleza, se omite la oración "Fortaleza: ...".
     *
     * @param nivel       nivel de logro: AD, A, B o C
     * @param competencia descripción de la competencia
     * @param fortaleza   frase de fortaleza (puede ser null o vacía)
     * @param debilidad   frase de debilidad (obligatoria para B y C)
     * @return conclusión descriptiva lista para guardar
     */
    public String generar(String nivel, String competencia, String fortaleza, String debilidad) {
        StringBuilder texto = new StringBuilder();
        texto.append("El estudiante alcanzó el nivel ").append(nivel)
                .append(" en ").append(competencia).append('.');
        if (tieneTexto(fortaleza)) {
            texto.append(" Fortaleza: ").append(fortaleza).append('.');
        }
        if (requiereDebilidad(nivel) && tieneTexto(debilidad)) {
            texto.append(" A mejorar: ").append(debilidad).append('.');
        }
        return texto.toString();
    }

    /**
     * @param valor texto a revisar
     * @return true si el texto no es null ni está vacío
     */
    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}